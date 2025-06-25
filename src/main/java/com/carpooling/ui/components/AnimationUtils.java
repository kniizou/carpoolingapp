package com.carpooling.ui.components;

/**
 * Animation utility class for smooth transitions
 */
public class AnimationUtils {
    
    /**
     * Ease-in-out function for smooth animations
     * @param t current time (0 to 1)
     * @return eased value (0 to 1)
     */
    public static double easeInOut(double t) {
        if (t < 0.5) {
            return 2 * t * t;
        } else {
            return -1 + (4 - 2 * t) * t;
        }
    }
    
    /**
     * Linear interpolation between two values
     * @param start starting value
     * @param end ending value
     * @param progress progress from 0 to 1
     * @return interpolated value
     */
    public static double lerp(double start, double end, double progress) {
        return start + (end - start) * progress;
    }
    
    /**
     * Smooth step function for gradual acceleration and deceleration
     * @param t input value (0 to 1)
     * @return smoothed value (0 to 1)
     */
    public static double smoothStep(double t) {
        return t * t * (3 - 2 * t);
    }
}
