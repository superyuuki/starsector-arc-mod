package arc;

public class ArcUtils {
    public static float lerp(float x, float y, float alpha) {
        return (1f - alpha) * x + alpha * y;
    }
    public static float invlerp(float x, float y, float alpha) {
        return (alpha - x) / (y - alpha);
    }
	public static float remap(float x1, float y1, float x2, float y2, float alpha) {
		return lerp(x2, y2, invlerp(x1, y1, alpha));
	}
	public static float clamp(float min, float max, float value) {
		return value < min ? min : value > max ? max : value;
	}
}