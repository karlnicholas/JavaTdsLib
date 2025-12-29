// Copyright (c) Microsoft Corporation.
// Licensed under the MIT License.

package com.microsoft.data.tools.tdslib.buffer;

import java.nio.ByteBuffer;
import java.util.List;

public final class ByteBufferUtils {
    private ByteBufferUtils() {}

    public static int length(ByteBuffer b) { return b == null ? 0 : b.capacity(); }

    public static ByteBuffer concat(ByteBuffer a, ByteBuffer b) {
        byte[] newArray = new byte[length(a) + length(b)];
        if (a != null) {
            a.position(0);
            a.get(newArray, 0, length(a));
        }
        if (b != null) {
            b.position(0);
            b.get(newArray, length(a), length(b));
        }
        return ByteBuffer.wrap(newArray);
    }

    public static ByteBuffer concat(List<ByteBuffer> buffers) {
        int total = buffers.stream().mapToInt(ByteBufferUtils::length).sum();
        byte[] arr = new byte[total];
        int pos = 0;
        for (ByteBuffer bb : buffers) {
            if (bb == null) continue;
            bb.position(0);
            bb.get(arr, pos, length(bb));
            pos += length(bb);
        }
        return ByteBuffer.wrap(arr);
    }

    public static int readUInt8(ByteBuffer b, int offset) { return b.get(offset) & 0xFF; }

    public static int readUInt16BE(ByteBuffer b, int offset) { return ((b.get(offset) & 0xFF) << 8) | (b.get(offset + 1) & 0xFF); }

    public static int readUInt16LE(ByteBuffer b, int offset) { return (b.get(offset) & 0xFF) | ((b.get(offset + 1) & 0xFF) << 8); }

    public static long readUInt32BE(ByteBuffer b, int offset) {
        return ((long)(b.get(offset) & 0xFF) << 24) | ((long)(b.get(offset + 1) & 0xFF) << 16) | ((long)(b.get(offset + 2) & 0xFF) << 8) | (long)(b.get(offset + 3) & 0xFF);
    }

    public static long readUInt32LE(ByteBuffer b, int offset) {
        return (long)(b.get(offset) & 0xFF) | ((long)(b.get(offset + 1) & 0xFF) << 8) | ((long)(b.get(offset + 2) & 0xFF) << 16) | ((long)(b.get(offset + 3) & 0xFF) << 24);
    }

    public static int writeUInt8(ByteBuffer b, int value, int offset) { b.put(offset, (byte) value); return offset + 1; }
    public static int writeUInt8(ByteBuffer b, int value) { return writeUInt8(b, value, 0); }

    public static int writeUInt16BE(ByteBuffer b, int value, int offset) { b.put(offset, (byte)((value >> 8) & 0xFF)); b.put(offset + 1, (byte)(value & 0xFF)); return offset + 2; }
    public static int writeUInt16LE(ByteBuffer b, int value, int offset) { b.put(offset, (byte)(value & 0xFF)); b.put(offset + 1, (byte)((value >> 8) & 0xFF)); return offset + 2; }

    public static int writeInt16LE(ByteBuffer b, int value, int offset) { return writeUInt16LE(b, value, offset); }

    public static int writeUInt32BE(ByteBuffer b, long value, int offset) { b.put(offset, (byte)((value >> 24) & 0xFF)); b.put(offset + 1, (byte)((value >> 16) & 0xFF)); b.put(offset + 2, (byte)((value >> 8) & 0xFF)); b.put(offset + 3, (byte)(value & 0xFF)); return offset + 4; }
    public static int writeUInt32LE(ByteBuffer b, long value, int offset) { b.put(offset, (byte)(value & 0xFF)); b.put(offset + 1, (byte)((value >> 8) & 0xFF)); b.put(offset + 2, (byte)((value >> 16) & 0xFF)); b.put(offset + 3, (byte)((value >> 24) & 0xFF)); return offset + 4; }
    public static int writeUInt32LE(ByteBuffer b, long value) { return writeUInt32LE(b, value, 0); }

    public static int writeInt32LE(ByteBuffer b, int value, int offset) { b.put(offset, (byte)(value & 0xFF)); b.put(offset + 1, (byte)((value >> 8) & 0xFF)); b.put(offset + 2, (byte)((value >> 16) & 0xFF)); b.put(offset + 3, (byte)((value >> 24) & 0xFF)); return offset + 4; }
    public static int writeInt32LE(ByteBuffer b, int value) { return writeInt32LE(b, value, 0); }

    public static int write(ByteBuffer dest, ByteBuffer src, int destinationOffset, int sourceOffset, int length) {
        byte[] tmp = new byte[length];
        src.position(sourceOffset);
        src.get(tmp, 0, length);
        dest.position(destinationOffset);
        dest.put(tmp, 0, length);
        return destinationOffset + length;
    }

    public static int write(ByteBuffer dest, ByteBuffer src, int offset) { return write(dest, src, offset, 0, length(src)); }

    public static ByteBuffer slice(ByteBuffer b, int startIndex, int length) { byte[] arr = new byte[length]; b.position(startIndex); b.get(arr, 0, length); return ByteBuffer.wrap(arr); }

    public static byte[] toArray(ByteBuffer b) { byte[] arr = new byte[length(b)]; b.position(0); b.get(arr); return arr; }
}
