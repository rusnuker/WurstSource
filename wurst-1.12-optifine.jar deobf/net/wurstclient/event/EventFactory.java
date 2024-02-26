// 
// Decompiled by Procyon v0.6.0
// 

package net.wurstclient.event;

import net.wurstclient.events.PlayerDamageBlockListener;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.wurstclient.events.IsNormalCubeListener;
import net.wurstclient.events.PacketInputListener;
import net.minecraft.network.Packet;
import net.wurstclient.events.RenderTileEntityListener;
import net.minecraft.tileentity.TileEntity;
import net.wurstclient.events.RenderBlockModelListener;
import net.wurstclient.events.ShouldSideBeRenderedListener;
import net.wurstclient.events.GetAmbientOcclusionLightValueListener;
import net.minecraft.block.state.IBlockState;
import net.wurstclient.events.SetOpaqueCubeListener;
import net.wurstclient.events.PlayerMoveListener;
import net.minecraft.client.entity.EntityPlayerSP;
import net.wurstclient.WurstClient;
import net.wurstclient.events.CameraTransformViewBobbingListener;

public final class EventFactory
{
    public static boolean cameraTransformViewBobbing() {
        final CameraTransformViewBobbingListener.CameraTransformViewBobbingEvent event = new CameraTransformViewBobbingListener.CameraTransformViewBobbingEvent();
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static void onPlayerMove(final EntityPlayerSP player) {
        WurstClient.INSTANCE.events.fire(new PlayerMoveListener.PlayerMoveEvent(player));
    }
    
    public static boolean setOpaqueCube() {
        final SetOpaqueCubeListener.SetOpaqueCubeEvent event = new SetOpaqueCubeListener.SetOpaqueCubeEvent();
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static float getAmbientOcclusionLightValue(final float f, final IBlockState state) {
        final GetAmbientOcclusionLightValueListener.GetAmbientOcclusionLightValueEvent event = new GetAmbientOcclusionLightValueListener.GetAmbientOcclusionLightValueEvent(state, f);
        WurstClient.INSTANCE.events.fire(event);
        return event.getLightValue();
    }
    
    public static boolean shouldSideBeRendered(final boolean b, final IBlockState state) {
        final ShouldSideBeRenderedListener.ShouldSideBeRenderedEvent event = new ShouldSideBeRenderedListener.ShouldSideBeRenderedEvent(state, b);
        WurstClient.INSTANCE.events.fire(event);
        return event.isRendered();
    }
    
    public static boolean renderBlockModel(final IBlockState state) {
        final RenderBlockModelListener.RenderBlockModelEvent event = new RenderBlockModelListener.RenderBlockModelEvent(state);
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static boolean renderTileEntity(final TileEntity tileEntity) {
        final RenderTileEntityListener.RenderTileEntityEvent event = new RenderTileEntityListener.RenderTileEntityEvent(tileEntity);
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static boolean onReceivePacket(final Packet packet) {
        final PacketInputListener.PacketInputEvent event = new PacketInputListener.PacketInputEvent(packet);
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static boolean isNormalCube() {
        final IsNormalCubeListener.IsNormalCubeEvent event = new IsNormalCubeListener.IsNormalCubeEvent();
        WurstClient.INSTANCE.events.fire(event);
        return !event.isCancelled();
    }
    
    public static void onPlayerDamageBlock(final BlockPos pos, final EnumFacing facing) {
        WurstClient.INSTANCE.events.fire(new PlayerDamageBlockListener.PlayerDamageBlockEvent(pos, facing));
    }
}
