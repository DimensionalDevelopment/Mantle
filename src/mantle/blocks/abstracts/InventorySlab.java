package mantle.blocks.abstracts;

import net.minecraft.block.material.Material;
import net.minecraft.entity.Entity;
import net.minecraft.util.AxisAlignedBB;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.World;
import java.util.List;

/**
 * Slab version of InventoryBlock.
 *
 * @author mDiyo
 */
public abstract class InventorySlab extends InventoryBlock
{
    public InventorySlab(Material material)
    {
        super(material);
    }

    /* Rendering */
    @Override
    //TODO:        renderAsNormalBlock()
    public boolean func_149686_d ()
    {
        return false;
    }

    //TODO:        isOpaqueCube()
    @Override
    public boolean func_149662_c ()
    {
        return false;
    }

    //TODO          shouldSideBeRendered
    @Override
    public boolean func_149646_a (IBlockAccess world, int x, int y, int z, int side)
    {
        if (side > 1)
            return super.func_149646_a(world, x, y, z, side);

        int meta = world.getBlockMetadata(x, y, z);
        boolean top = (meta | 8) == 1;
        if ((top && side == 0) || (!top && side == 1))
            return true;

        return super.func_149646_a(world, x, y, z, side);
    }
    
    //TODO:     addCollisionBoxesToList()
    @Override
    public void func_149743_a (World world, int x, int y, int z, AxisAlignedBB axisalignedbb, List arraylist, Entity entity)
    {
        setBlockBoundsBasedOnState(world, x, y, z);
        super.func_149743_a(world, x, y, z, axisalignedbb, arraylist, entity);
    }

    public void setBlockBoundsForItemRender ()
    {
        //TODO: setBlockBounds     
        func_149676_a(0.0F, 0.0F, 0.0F, 1.0F, 0.5F, 1.0F);
    }

    public void setBlockBoundsBasedOnState (IBlockAccess world, int x, int y, int z)
    {
        int meta = world.getBlockMetadata(x, y, z) / 8;
        float minY = meta == 1 ? 0.5F : 0.0F;
        float maxY = meta == 1 ? 1.0F : 0.5F;
        func_149676_a(0.0F, minY, 0F, 1.0F, maxY, 1.0F);
    }

    public int onBlockPlaced (World par1World, int blockX, int blockY, int blockZ, int side, float clickX, float clickY, float clickZ, int metadata)
    {
        if (side == 1)
            return metadata;
        if (side == 0 || clickY >= 0.5F)
            return metadata | 8;

        return metadata;
    }

    public int damageDropped (int meta)
    {
        return meta % 8;
    }
}