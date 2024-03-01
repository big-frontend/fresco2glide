package com.facebook.drawee.drawable;

public class ScalingUtils {
    public interface ScaleType {

        /**
         * Scales width and height independently, so that the child matches the parent exactly. This may
         * change the aspect ratio of the child.
         */
        int FIT_XY = 1;

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the top-left corner
         * of the parent.
         */
        int FIT_START = 2;

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is centered within the parent's
         * bounds.
         */
        int FIT_CENTER = 3;

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the bottom-right
         * corner of the parent.
         */
        int FIT_END =4;

        /** Performs no scaling. Child is centered within parent's bounds. */
        int CENTER = 5;
        /**
         * Scales the child so that both dimensions will be greater than or equal to the corresponding
         * dimension of the parent. At least one dimension (width or height) will fit exactly. Child is
         * centered within parent's bounds.
         */
        int CENTER_CROP = 6;

        /**
         * Scales the child so that it fits entirely inside the parent. Unlike FIT_CENTER, if the child
         * is smaller, no up-scaling will be performed. Aspect ratio is preserved. Child is centered
         * within parent's bounds.
         */
        int CENTER_INSIDE = 7;

        /**
         * Scales the child so that both dimensions will be greater than or equal to the corresponding
         * dimension of the parent. At least one dimension (width or height) will fit exactly. The
         * child's focus point will be centered within the parent's bounds as much as possible without
         * leaving empty space. It is guaranteed that the focus point will be visible and centered as
         * much as possible. If the focus point is set to (0.5f, 0.5f), result will be equivalent to
         * CENTER_CROP.
         */
        int FOCUS_CROP = 8;

        /**
         * Scales the child so that it fits entirely inside the parent. At least one dimension (width or
         * height) will fit exactly. Aspect ratio is preserved. Child is aligned to the bottom-left
         * corner of the parent.
         */
        int FIT_BOTTOM_START = 9;
        int CIRCLE_CROP = 10;
    }
}
