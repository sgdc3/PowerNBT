package me.dpohvar.powernbt.api;

import me.dpohvar.powernbt.utils.*;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.inventory.ItemStack;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

/**
 * PowerNBT API.<br>
 * This class has methods to read and write NBT tags.<br>
 * {@link me.dpohvar.powernbt.api.NBTCompound} is used to work with NBTTagCompound,<br>
 * {@link me.dpohvar.powernbt.api.NBTList} is used to work with NBTTagList,<br>
 * {@link java.lang.String} is used to work with NBTTagString,<br>
 * all other NBT tags represents by primitive values.<br>
 * @since 0.7.1
 */

@SuppressWarnings("UnusedDeclaration")
public class NBTManager {

    public static final NBTManager nbtManager = new NBTManager();

    /**
     * Get single instance of {@link me.dpohvar.powernbt.api.NBTManager}.
     *
     * @return NBTManager
     */
    public static NBTManager getInstance() {
        return nbtManager;
    }

    EntityUtils entityUtils = EntityUtils.entityUtils;
    ItemStackUtils itemStackUtils = ItemStackUtils.itemStackUtils;
    NBTBlockUtils nbtBlockUtils = NBTBlockUtils.nbtBlockUtils;
    NBTCompressedUtils nbtCompressedUtils = NBTCompressedUtils.nbtCompressedUtils;
    NBTUtils nbtUtils = NBTUtils.nbtUtils;
    ChunkUtils chunkUtils = ChunkUtils.chunkUtils;
    ReflectionUtils.RefMethod getUUID;

    private NBTManager(){
        try {
            getUUID = ReflectionUtils.getRefClass(OfflinePlayer.class).findMethodByReturnType(UUID.class);
        } catch (Exception ignored) {
            // can't use UUID, do nothing
        }
    }

    /**
     * Read NBT tag of bukkit entity.
     *
     * @param entity Entity to read
     * @return Nbt tag of bukkit entity
     */
    public NBTCompound read(Entity entity){
        NBTCompound compound = new NBTCompound();
        entityUtils.readEntity(entity, compound.getHandle());
        return compound;
    }

    /**
     * Store nbt data to entity.
     *
     * @param entity Entity to modify
     * @param compound Nbt data to be stored
     */
    public void write(Entity entity, NBTCompound compound){
        entityUtils.writeEntity(entity, compound.getHandle());
    }

    /**
     * Read extra nbt data of entity.<br>
     * Works with forge only.
     *
     * @param entity Entity to read
     * @return Extra nbt data. null if no forge
     */
    public NBTCompound readForgeData(Entity entity){
        Object tag = entityUtils.getForgeData(entity);
        if (tag==null) return new NBTCompound();
        else return NBTCompound.forNBTCopy(tag);
    }

    /**
     * Store extra nbt data to entity.
     *
     * Works with forge only
     * @param entity entity
     * @param compound extra nbt data
     */
    public void writeForgeData(Entity entity, NBTCompound compound){
        entityUtils.setForgeData(entity, compound.getHandleCopy());
    }

    /**
     * Read nbt tag of {@link org.bukkit.inventory.ItemStack}.
     *
     * @param item Bukkit {@link org.bukkit.inventory.ItemStack}
     * @return Nbt data. null if item has no nbt and no meta
     */
    public NBTCompound read(ItemStack item){
        Object tag = ItemStackUtils.itemStackUtils.getTag(item);
        return NBTCompound.forNBTCopy(tag);
    }
    /**
     * Read nbt tag of {@link org.bukkit.Chunk}.
     *
     * @param chunk Bukkit chunk
     * @return Nbt data of chunk
     * @since 0.8.1
     */
    public NBTCompound read(Chunk chunk){
        NBTCompound compound = new NBTCompound();
        chunkUtils.readChunk(chunk, compound.getHandle());
        return compound;
    }

    /**
     * Store nbt tag to selected chunk.
     *
     * @param chunk Chunk to be changed
     * @param compound Nbt data
     * @since 0.8.1
     */
    public void write(Chunk chunk, NBTCompound compound){
        chunkUtils.writeChunk(chunk, compound.getHandle());
    }

    /**
     * Save nbt tag to item stack.<br>
     * You can save any data to CraftItemStack.<br>
     * You can save to ItemStack only data allowed by {@link org.bukkit.inventory.meta.ItemMeta}.<br>
     *
     * @param item bukkit item stack
     * @param compound tag
     */
    public void write(ItemStack item, NBTCompound compound){
        itemStackUtils.setTag(item, compound.getHandleCopy());
    }

    /**
     * Read nbt data of tile entity at block.
     *
     * @param block Block with tile entity
     * @return Nbt data of tile entity or empty compound if no data
     */
    public NBTCompound read(Block block){
        NBTCompound compound = new NBTCompound();
        nbtBlockUtils.readTag(block,compound.getHandle());
        return compound;
    }

    /**
     * Save nbt data to tile entity at block.
     *
     * @param block Block with tile entity
     * @param compound Tag to be saved
     */
    public void write(Block block, NBTCompound compound){
        nbtBlockUtils.setTag(block, compound.getHandle());
        nbtBlockUtils.update(block);
    }

    /**
     * Read raw NBT data from input stream and convert to java object.
     *
     * @param inputStream InputStream to read
     * @return Read object
     * @throws IOException it happens sometimes
     */
    public Object read(InputStream inputStream) throws IOException {
        return read((DataInput) new DataInputStream(inputStream));
    }

    /**
     * Convert java object to nbt and write to outputStream.<br>
     * Allowed all primitive types, collections and maps.
     *
     * @param outputStream outputStream to write
     * @param value value to be written
     * @throws IOException it happens sometimes
     */
    public void write(OutputStream outputStream, Object value) throws IOException {
        write((DataOutput) new DataOutputStream(outputStream), value);
    }

    /**
     * Read compressed nbt compound.
     *
     * @param inputStream InputStream to read
     * @return Nbt rag
     */
    public NBTCompound readCompressed(InputStream inputStream){
        Object tag = nbtCompressedUtils.readCompound(inputStream);
        return NBTCompound.forNBT(tag);
    }

    /**
     * Compress nbt compound and write to outputStream.
     *
     * @param outputStream outputStream to write
     * @param value value
     */
    public void writeCompressed(OutputStream outputStream, NBTCompound value){
        nbtCompressedUtils.writeCompound(value.getHandle(), outputStream);
    }

    /**
     * Read nbt data from dataInput and convert to java object.
     *
     * @param dataInput dataInput to read
     * @return nbt data converted to java object
     * @throws IOException it happens
     */
    public Object read(DataInput dataInput) throws IOException {
        Object tag =  nbtUtils.readTag(dataInput);
        return nbtUtils.getValue(tag);
    }

    /**
     * Convert value to nbt and write to dataOutput.
     *
     * @param dataOutput dataOutput to save
     * @param value value to be written
     * @throws IOException it happens sometimes
     */
    public void write(DataOutput dataOutput, Object value) throws IOException {
        Object tag =  nbtUtils.createTag(value);
        nbtUtils.writeTagToOutput(dataOutput, tag);
    }

    /**
     * Read raw nbt data from file and convert to java object.
     *
     * @param file file to read
     * @return nbt data converted to java types
     * @throws IOException it happens
     */
    public Object read(File file) throws IOException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return read(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    /**
     * Write to file value converted to nbt tag.
     *
     * @param file file to write
     * @param value value to be written
     * @throws IOException it happens
     */
    public void write(File file, Object value) throws IOException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            write(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file "+file, e);
            }
        }
    }

    /**
     * Read compressed nbt data from file and convert to java object.
     *
     * @param file file to read
     * @return nbt data converted to java types
     * @throws FileNotFoundException if file not found
     */
    public NBTCompound readCompressed(File file) throws FileNotFoundException {
        InputStream inputStream = null;
        try {
            inputStream = new FileInputStream(file);
            return readCompressed(inputStream);
        } finally {
            if (inputStream != null) try {
                inputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file "+file, e);
            }
        }
    }

    /**
     * Convert value to nbt and write to file with compression.
     *
     * @param file file to write
     * @param value value to be written
     * @throws FileNotFoundException check your file
     */
    public void writeCompressed(File file, NBTCompound value) throws FileNotFoundException {
        OutputStream outputStream = null;
        try {
            outputStream = new FileOutputStream(file);
            writeCompressed(outputStream, value);
        } finally {
            if (outputStream != null) try {
                outputStream.close();
            } catch (IOException e) {
                Bukkit.getLogger().log(Level.ALL, "can not close NBT file " + file, e);
            }
        }
    }

    /**
     * Read offline player's .dat file.
     *
     * @param player player to read
     * @return nbt data read from a file
     */
    public NBTCompound readOfflinePlayer(OfflinePlayer player){
        File file = getPlayerFile(player);
        try{
            return readCompressed(file);
        } catch (FileNotFoundException e) {
            return null;
        }
    }

    /**
     * Read offline player's .dat file.
     *
     * @param player player name
     * @return nbt data read from a file
     */
    @SuppressWarnings("deprecation")
    public NBTCompound readOfflinePlayer(String player){
        return readOfflinePlayer(Bukkit.getOfflinePlayer(player));
    }

    /**
     * Write nbt data to player's .dat file.
     *
     * @param player offline player
     * @param value value to be written
     * @return true on success, false otherwise
     */
    public boolean writeOfflinePlayer(OfflinePlayer player, NBTCompound value){
        File file = getPlayerFile(player);
        try{
            writeCompressed(file, value);
            return true;
        } catch (FileNotFoundException e) {
            return false;
        }
    }

    /**
     * Write nbt data to player's .dat file.
     *
     * @param player player name
     * @param value value to be written
     * @return true on success, false otherwise
     */
    @SuppressWarnings("deprecation")
    public boolean writeOfflinePlayer(String player, NBTCompound value){
        return writeOfflinePlayer(Bukkit.getPlayer(player), value);
    }

    /**
     * Get {@link org.bukkit.OfflinePlayer} file with stored nbt data.
     *
     * @param player offline player
     * @return player's file
     */
    public File getPlayerFile(OfflinePlayer player){
        File baseDir = (Bukkit.getWorlds().get(0)).getWorldFolder();
        if (getUUID != null) {
            UUID uuid = player.getUniqueId();
            File playerDir = new File(baseDir, "playerdata");
            return new File(playerDir, uuid+".dat");
        } else {
            File playerDir = new File(baseDir, "players");
            return new File(playerDir, player.getName() + ".dat");
        }
    }

    /**
     * Parse mojangson string.<br>
     * This method can return:<br>
     * byte, short, int, long, float, double, byte[], String, int[],<br>
     * {@link me.dpohvar.powernbt.api.NBTList} or {@link me.dpohvar.powernbt.api.NBTCompound}<br>
     * Examples:<br> <pre>
     *     manager.parseMojangson("12s\\"); // 12 short
     *     manager.parseMojangson("{foo:bar}"); // NBTCompound
     * </pre>
     *
     * @param value String in Mojangson format
     * @return Parse result
     * @since 0.8.2
     */
    public Object parseMojangson(String value){
        if (value == null) return null;
        Object tag = NBTParser.parser("", value).parse();
        return nbtUtils.getValue(tag);
    }

    /**
     * Spawn entity in world by nbt compound.<br>
     * Entity location must be stored in "Pos" tag.
     *
     * @param compound Entity data
     * @param world World where to spawn entity
     * @return Spawned entity
     * @since 0.8.2
     */
    public Entity spawnEntity(NBTCompound compound, World world){
        if (compound == null) return null;
        return entityUtils.spawnEntity(compound.getHandle(), world);
    }

    /**
     * Convert bukkit ItemStack to CraftItemStack.
     *
     * @param itemStack Bukkit ItemStack
     * @return CraftItemStack with nms item
     */
    public ItemStack asCraftItemStack(ItemStack itemStack){
        return itemStackUtils.createCraftItemStack(itemStack);
    }

    static boolean checkCrossReferences(LinkedList<Object> list, Collection values){
        for (Object value : values) {
            if (list.contains(value)) return true;
            if (value instanceof Collection) {
                list.push(value);
                if (checkCrossReferences(list, (Collection)value)) return true;
                list.pop();
            } else if (value instanceof Map) {
                list.push(value);
                if (checkCrossReferences(list, ((Map)value).values())) return true;
                list.pop();
            } else if (value instanceof Object[]) {
                list.push(value);
                if (checkCrossReferences(list, Arrays.asList((Object[])value))) return true;
                list.pop();
            }
        }
        return false;
    }

    static boolean checkCrossReferences(Map map){
        LinkedList<Object> list = new LinkedList<Object>();
        list.push(map);
        return checkCrossReferences(list, map.values());
    }

    static boolean checkCrossReferences(Collection collection){
        LinkedList<Object> list = new LinkedList<Object>();
        list.push(collection);
        return checkCrossReferences(list, collection);
    }

    static boolean checkCrossReferences(Object[] collection){
        LinkedList<Object> list = new LinkedList<Object>();
        list.push(collection);
        return checkCrossReferences(list, Arrays.asList(collection));
    }

}
