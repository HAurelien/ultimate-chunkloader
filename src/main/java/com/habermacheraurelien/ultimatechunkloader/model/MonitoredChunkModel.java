package com.habermacheraurelien.ultimatechunkloader.model;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.world.level.ChunkPos;

import java.util.UUID;

/**
 * Represents a chunk that is being monitored in the world,
 * along with the player responsible for it.
 *
 * <p>This class is used both for logic and serialization (via {@link #chunkAnchorBlockModelCodec})
 * to sync or persist chunk-related data on a per-player basis.</p>
 */
public class MonitoredChunkModel {

    /** The chunk position in the world. */
    public final ChunkPos chunkPos;

    /** The UUID of the player responsible for monitoring this chunk. */
    public UUID playerIdResponsible;

    /**
     * Constructs a model with a given chunk position and no responsible player yet assigned.
     *
     * @param _chunkPos the chunk position
     */
    public MonitoredChunkModel(ChunkPos _chunkPos) {
        this.chunkPos = _chunkPos;
    }

    /**
     * Constructs a model with a given chunk position and responsible player UUID.
     *
     * @param _chunkPos          the chunk position
     * @param _playerResponsible the UUID of the player responsible
     */
    public MonitoredChunkModel(ChunkPos _chunkPos, UUID _playerResponsible) {
        this.chunkPos = _chunkPos;
        this.playerIdResponsible = _playerResponsible;
    }

    /**
     * Constructs a model using raw x/z coordinates and a string UUID.
     * Used mainly during deserialization.
     *
     * @param _x                 the x coordinate of the chunk
     * @param _z                 the z coordinate of the chunk
     * @param _playerIdResponsible string representation of the player's UUID
     */
    public MonitoredChunkModel(Integer _x, Integer _z, String _playerIdResponsible) {
        this.chunkPos = new ChunkPos(_x, _z);
        this.playerIdResponsible = UUID.fromString(_playerIdResponsible);
    }

    /**
     * Checks if the provided position matches this model's chunk position.
     *
     * @param _chunkPos the position to compare
     * @return true if the position matches this model's chunk, false otherwise
     */
    public boolean hasPos(ChunkPos _chunkPos) {
        return _chunkPos.x == chunkPos.x && _chunkPos.z == chunkPos.z;
    }

    /**
     * Checks whether the specified player UUID is responsible for this chunk.
     *
     * @param playerId the UUID to compare
     * @return true if the given UUID matches the responsible player's UUID
     */
    public boolean isPlayerResponsible(UUID playerId) {
        return playerId.equals(playerIdResponsible);
    }

    /**
     * Returns the x coordinate of the chunk.
     *
     * @return the x position
     */
    private int getPosX() {
        return chunkPos.x;
    }

    /**
     * Returns the z coordinate of the chunk.
     *
     * @return the z position
     */
    private int getPosZ() {
        return chunkPos.z;
    }

    /**
     * Returns the responsible player's UUID as a string.
     *
     * @return the UUID string
     */
    private String getPlayerUUIDAsString() {
        return playerIdResponsible.toString();
    }

    /**
     * Codec used to serialize and deserialize instances of {@link MonitoredChunkModel}.
     * Encodes the chunk's X and Z positions along with the responsible player's UUID.
     */
    public static final Codec<MonitoredChunkModel> chunkAnchorBlockModelCodec = RecordCodecBuilder
            .create(instance -> instance.group(
                    Codec.INT.fieldOf("x").forGetter(MonitoredChunkModel::getPosX),
                    Codec.INT.fieldOf("z").forGetter(MonitoredChunkModel::getPosZ),
                    Codec.STRING.fieldOf("uuid").forGetter(MonitoredChunkModel::getPlayerUUIDAsString)
            ).apply(instance, MonitoredChunkModel::new));
}