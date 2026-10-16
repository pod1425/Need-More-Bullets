package net.pod.cnmb.mixin;

import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPoint;
import com.simibubi.create.content.kinetics.mechanicalArm.ArmInteractionPointHandler;
import net.createmod.catnip.outliner.Outline;
import net.createmod.catnip.outliner.Outliner;
import net.minecraft.world.phys.AABB;
import net.pod.cnmb.block.pallet.PalletBlock;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(ArmInteractionPointHandler.class)
public class PalletBlockArmOutlineMixin {
    @Redirect(method = "drawOutlines", at = @At(value = "INVOKE", target = "Lnet/createmod/catnip/outliner/Outliner;showAABB(Ljava/lang/Object;Lnet/minecraft/world/phys/AABB;)Lnet/createmod/catnip/outliner/Outline$OutlineParams;"))
    private static Outline.OutlineParams modifyOutline(Outliner instance, Object slot, AABB bb) {
        if (slot instanceof ArmInteractionPoint point) {
            var pos = point.getPos();
            var state = point.getLevel().getBlockState(pos);
            if (state.getBlock() instanceof PalletBlock) {
                return instance.showAABB(slot, PalletBlock.getDefaultShape().bounds().move(pos));
            }
        }
        return instance.showAABB(slot, bb);
    }
}
