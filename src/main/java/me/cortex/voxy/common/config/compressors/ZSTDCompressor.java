package me.cortex.voxy.common.config.compressors;

import com.github.luben.zstd.Zstd;
import me.cortex.voxy.common.config.ConfigBuildCtx;
import me.cortex.voxy.common.util.MemoryBuffer;
import me.cortex.voxy.common.util.ThreadLocalMemoryBuffer;
import me.cortex.voxy.common.world.SaveLoadSystem;

import java.nio.ByteBuffer;

public class ZSTDCompressor implements StorageCompressor {
    private static final ThreadLocalMemoryBuffer SCRATCH = new ThreadLocalMemoryBuffer(SaveLoadSystem.BIGGEST_SERIALIZED_SECTION_SIZE + 1024);

    private final int level;

    public ZSTDCompressor(int level) {
        this.level = level;
    }

    @Override
    public MemoryBuffer compress(MemoryBuffer saveData) {
        int srcSize = (int) saveData.size;
        byte[] input = new byte[srcSize];
        ByteBuffer inBuf = saveData.asByteBuffer();
        inBuf.get(input);

        byte[] compressed = Zstd.compress(input, this.level);

        MemoryBuffer out = new MemoryBuffer(compressed.length);
        out.asByteBuffer().put(compressed);
        return out;
    }

    @Override
    public MemoryBuffer decompress(MemoryBuffer saveData) {
        int srcSize = (int) saveData.size;
        byte[] input = new byte[srcSize];
        ByteBuffer inBuf = saveData.asByteBuffer();
        inBuf.get(input);

        long expected = Zstd.decompressedSize(input);
        if (expected <= 0) {
            throw new RuntimeException("Unable to determine decompressed size");
        }
        byte[] decompressed = new byte[(int) expected];
        long result = Zstd.decompress(decompressed, input);
        if (Zstd.isError(result)) {
            throw new RuntimeException("Zstd decompression failed: " + Zstd.getErrorName(result));
        }

        var buf = SCRATCH.get().createUntrackedUnfreeableReference();
        buf.asByteBuffer().put(decompressed);
        return buf.subSize(decompressed.length);
    }

    @Override
    public void close() {
    }

    public static class Config extends CompressorConfig {
        public int compressionLevel;

        @Override
        public StorageCompressor build(ConfigBuildCtx ctx) {
            return new ZSTDCompressor(this.compressionLevel);
        }

        public static String getConfigTypeName() {
            return "ZSTD";
        }
    }
}
