package com.microape.wifihelper.socket.core.iocore;

import com.microape.wifihelper.socket.core.exceptions.ReadException;
import com.microape.wifihelper.socket.core.iocore.interfaces.IOAction;
import com.microape.wifihelper.socket.core.pojo.OriginalData;
import com.microape.wifihelper.socket.core.protocol.IReaderProtocol;
import com.microape.wifihelper.socket.core.utils.BytesUtils;
import com.microape.wifihelper.socket.core.utils.SLog;

import java.io.IOException;
import java.nio.ByteBuffer;


/**
 * Created by xuhao on 2017/5/31.
 */

public class ReaderImpl extends AbsReader {

    private ByteBuffer mRemainingBuf;

    private final int bufferLength = 1024;
    private ByteBuffer byteBuffer = ByteBuffer.allocate(bufferLength);

    @Override
    public void read() throws RuntimeException {
        OriginalData originalData = new OriginalData();
        byteBuffer.clear();
        try {
            readHeaderFromChannel();
            readBodyFromChannel();

            int bodyLength = byteBuffer.position();
            byte[] bodyByte = new byte[bodyLength];
            byteBuffer.flip();
            byteBuffer.get(bodyByte, 0, bodyLength);

            originalData.setHeadBytes(bodyByte.length);
            originalData.setBodyBytes(bodyByte);
            mStateSender.sendBroadcast(IOAction.ACTION_READ_COMPLETE, originalData);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void readHeaderFromChannel() throws IOException {
        byte[] bytes = new byte[1];
        while (true){
            int value = mInputStream.read(bytes);
            System.out.print(">>>>>>>>readHeaderFromChannel: byte[0] = 0x"+Integer.toHexString(bytes[0]));
            if (value == -1) {
                throw new ReadException(
                        "read head is wrong, this socket input stream is end of file read " + value + " ,that mean this socket is disconnected by server");
            }
            if (bytes[0] == 0x02){
                break;
            }
        }
    }

    private void readBodyFromChannel() throws IOException {
        byte[] bufArray = new byte[1];
        byteBuffer.put((byte) 0x02);
        try {
            while (byteBuffer.hasRemaining()) {
                int len = mInputStream.read(bufArray);
                if (len == -1) {
                    break;
                }
                byteBuffer.put(bufArray[0]);
                if (bufArray[0] == 0x03){
                    break;
                }
            }
        } catch (Exception e) {
            throw e;
        }
        if (SLog.isDebug()) {
            SLog.i("read total bytes: " + BytesUtils.toHexStringForLog(byteBuffer.array()));
            SLog.i("read total length:" + (byteBuffer.capacity() - byteBuffer.remaining()));
        }
    }

    @Deprecated
    public void read1() throws RuntimeException {
        OriginalData originalData = new OriginalData();
        IReaderProtocol headerProtocol = mOkOptions.getReaderProtocol();
        ByteBuffer headBuf = ByteBuffer.allocate(headerProtocol.getHeaderLength());
        headBuf.order(mOkOptions.getReadByteOrder());
        try {
            if (mRemainingBuf != null) {
                mRemainingBuf.flip();
                int length = Math.min(mRemainingBuf.remaining(), headerProtocol.getHeaderLength());
                headBuf.put(mRemainingBuf.array(), 0, length);
                if (length < headerProtocol.getHeaderLength()) {
                    //there are no data left
                    mRemainingBuf = null;
                    readHeaderFromChannel(headBuf, headerProtocol.getHeaderLength() - length);
                } else {
                    mRemainingBuf.position(headerProtocol.getHeaderLength());
                }
            } else {
                readHeaderFromChannel(headBuf, headBuf.capacity());
            }
            originalData.setHeadBytes(headBuf.array());
            if (SLog.isDebug()) {
                SLog.i("read head: " + BytesUtils.toHexStringForLog(headBuf.array()));
            }
            int bodyLength = headerProtocol.getBodyLength(originalData.getHeadBytes(), mOkOptions.getReadByteOrder());
            if (SLog.isDebug()) {
                SLog.i("need read body length: " + bodyLength);
            }
            if (bodyLength > 0) {
                if (bodyLength > mOkOptions.getMaxReadDataMB() * 1024 * 1024) {
                    throw new ReadException("Need to follow the transmission protocol.\r\n" +
                            "Please check the client/server code.\r\n" +
                            "According to the packet header data in the transport protocol, the package length is " + bodyLength + " Bytes.\r\n" +
                            "You need check your <ReaderProtocol> definition");
                }
                ByteBuffer byteBuffer = ByteBuffer.allocate(bodyLength);
                byteBuffer.order(mOkOptions.getReadByteOrder());
                if (mRemainingBuf != null) {
                    int bodyStartPosition = mRemainingBuf.position();
                    int length = Math.min(mRemainingBuf.remaining(), bodyLength);
                    byteBuffer.put(mRemainingBuf.array(), bodyStartPosition, length);
                    mRemainingBuf.position(bodyStartPosition + length);
                    if (length == bodyLength) {
                        if (mRemainingBuf.remaining() > 0) {//there are data left
                            ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                            temp.order(mOkOptions.getReadByteOrder());
                            temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                            mRemainingBuf = temp;
                        } else {//there are no data left
                            mRemainingBuf = null;
                        }
                        //cause this time data from remaining buffer not from channel.
                        originalData.setBodyBytes(byteBuffer.array());
                        mStateSender.sendBroadcast(IOAction.ACTION_READ_COMPLETE, originalData);
                        return;
                    } else {//there are no data left in buffer and some data pieces in channel
                        mRemainingBuf = null;
                    }
                }
                readBodyFromChannel(byteBuffer);
                originalData.setBodyBytes(byteBuffer.array());
            } else if (bodyLength == 0) {
                originalData.setBodyBytes(new byte[0]);
                if (mRemainingBuf != null) {
                    //the body is empty so header remaining buf need set null
                    if (mRemainingBuf.hasRemaining()) {
                        ByteBuffer temp = ByteBuffer.allocate(mRemainingBuf.remaining());
                        temp.order(mOkOptions.getReadByteOrder());
                        temp.put(mRemainingBuf.array(), mRemainingBuf.position(), mRemainingBuf.remaining());
                        mRemainingBuf = temp;
                    } else {
                        mRemainingBuf = null;
                    }
                }
            } else if (bodyLength < 0) {
                throw new ReadException(
                        "read body is wrong,this socket input stream is end of file read " + bodyLength + " ,that mean this socket is disconnected by server");
            }
            mStateSender.sendBroadcast(IOAction.ACTION_READ_COMPLETE, originalData);
        } catch (Exception e) {
            ReadException readException = new ReadException(e);
            throw readException;
        }
    }

    @Deprecated
    private void readHeaderFromChannel(ByteBuffer headBuf, int readLength) throws IOException {
        for (int i = 0; i < readLength; i++) {
            byte[] bytes = new byte[1];
            int value = mInputStream.read(bytes);
            if (value == -1) {
                throw new ReadException(
                        "read head is wrong, this socket input stream is end of file read " + value + " ,that mean this socket is disconnected by server");
            }
            headBuf.put(bytes);
        }
    }

    @Deprecated
    private void readBodyFromChannel(ByteBuffer byteBuffer) throws IOException {

        while (byteBuffer.hasRemaining()) {
            try {
                byte[] bufArray = new byte[mOkOptions.getReadPackageBytes()];
                int len = mInputStream.read(bufArray);
                if (len == -1) {
                    break;
                }
                int remaining = byteBuffer.remaining();
                if (len > remaining) {
                    byteBuffer.put(bufArray, 0, remaining);
                    mRemainingBuf = ByteBuffer.allocate(len - remaining);
                    mRemainingBuf.order(mOkOptions.getReadByteOrder());
                    mRemainingBuf.put(bufArray, remaining, len - remaining);
                } else {
                    byteBuffer.put(bufArray, 0, len);
                }
            } catch (Exception e) {
                throw e;
            }
        }
        if (SLog.isDebug()) {
            SLog.i("read total bytes: " + BytesUtils.toHexStringForLog(byteBuffer.array()));
            SLog.i("read total length:" + (byteBuffer.capacity() - byteBuffer.remaining()));
        }
    }

}
