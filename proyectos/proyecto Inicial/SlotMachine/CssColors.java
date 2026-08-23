import java.awt.Color;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class holding the full set of CSS standard color names and
 * their RGB values. Used both to validate symbol colors and to resolve
 * them to a real java.awt.Color for drawing.
 */
public class CssColors {
    private static final Map<String, Integer> NAMED_COLORS = new HashMap<>();

    static {
        NAMED_COLORS.put("aliceblue", 0xF0F8FF);
        NAMED_COLORS.put("antiquewhite", 0xFAEBD7);
        NAMED_COLORS.put("aqua", 0x00FFFF);
        NAMED_COLORS.put("aquamarine", 0x7FFFD4);
        NAMED_COLORS.put("azure", 0xF0FFFF);
        NAMED_COLORS.put("beige", 0xF5F5DC);
        NAMED_COLORS.put("bisque", 0xFFE4C4);
        NAMED_COLORS.put("black", 0x000000);
        NAMED_COLORS.put("blanchedalmond", 0xFFEBCD);
        NAMED_COLORS.put("blue", 0x0000FF);
        NAMED_COLORS.put("blueviolet", 0x8A2BE2);
        NAMED_COLORS.put("brown", 0xA52A2A);
        NAMED_COLORS.put("burlywood", 0xDEB887);
        NAMED_COLORS.put("cadetblue", 0x5F9EA0);
        NAMED_COLORS.put("chartreuse", 0x7FFF00);
        NAMED_COLORS.put("chocolate", 0xD2691E);
        NAMED_COLORS.put("coral", 0xFF7F50);
        NAMED_COLORS.put("cornflowerblue", 0x6495ED);
        NAMED_COLORS.put("cornsilk", 0xFFF8DC);
        NAMED_COLORS.put("crimson", 0xDC143C);
        NAMED_COLORS.put("cyan", 0x00FFFF);
        NAMED_COLORS.put("darkblue", 0x00008B);
        NAMED_COLORS.put("darkcyan", 0x008B8B);
        NAMED_COLORS.put("darkgoldenrod", 0xB8860B);
        NAMED_COLORS.put("darkgray", 0xA9A9A9);
        NAMED_COLORS.put("darkgreen", 0x006400);
        NAMED_COLORS.put("darkgrey", 0xA9A9A9);
        NAMED_COLORS.put("darkkhaki", 0xBDB76B);
        NAMED_COLORS.put("darkmagenta", 0x8B008B);
        NAMED_COLORS.put("darkolivegreen", 0x556B2F);
        NAMED_COLORS.put("darkorange", 0xFF8C00);
        NAMED_COLORS.put("darkorchid", 0x9932CC);
        NAMED_COLORS.put("darkred", 0x8B0000);
        NAMED_COLORS.put("darksalmon", 0xE9967A);
        NAMED_COLORS.put("darkseagreen", 0x8FBC8F);
        NAMED_COLORS.put("darkslateblue", 0x483D8B);
        NAMED_COLORS.put("darkslategray", 0x2F4F4F);
        NAMED_COLORS.put("darkslategrey", 0x2F4F4F);
        NAMED_COLORS.put("darkturquoise", 0x00CED1);
        NAMED_COLORS.put("darkviolet", 0x9400D3);
        NAMED_COLORS.put("deeppink", 0xFF1493);
        NAMED_COLORS.put("deepskyblue", 0x00BFFF);
        NAMED_COLORS.put("dimgray", 0x696969);
        NAMED_COLORS.put("dimgrey", 0x696969);
        NAMED_COLORS.put("dodgerblue", 0x1E90FF);
        NAMED_COLORS.put("firebrick", 0xB22222);
        NAMED_COLORS.put("floralwhite", 0xFFFAF0);
        NAMED_COLORS.put("forestgreen", 0x228B22);
        NAMED_COLORS.put("fuchsia", 0xFF00FF);
        NAMED_COLORS.put("gainsboro", 0xDCDCDC);
        NAMED_COLORS.put("ghostwhite", 0xF8F8FF);
        NAMED_COLORS.put("gold", 0xFFD700);
        NAMED_COLORS.put("goldenrod", 0xDAA520);
        NAMED_COLORS.put("gray", 0x808080);
        NAMED_COLORS.put("green", 0x008000);
        NAMED_COLORS.put("greenyellow", 0xADFF2F);
        NAMED_COLORS.put("grey", 0x808080);
        NAMED_COLORS.put("honeydew", 0xF0FFF0);
        NAMED_COLORS.put("hotpink", 0xFF69B4);
        NAMED_COLORS.put("indianred", 0xCD5C5C);
        NAMED_COLORS.put("indigo", 0x4B0082);
        NAMED_COLORS.put("ivory", 0xFFFFF0);
        NAMED_COLORS.put("khaki", 0xF0E68C);
        NAMED_COLORS.put("lavender", 0xE6E6FA);
        NAMED_COLORS.put("lavenderblush", 0xFFF0F5);
        NAMED_COLORS.put("lawngreen", 0x7CFC00);
        NAMED_COLORS.put("lemonchiffon", 0xFFFACD);
        NAMED_COLORS.put("lightblue", 0xADD8E6);
        NAMED_COLORS.put("lightcoral", 0xF08080);
        NAMED_COLORS.put("lightcyan", 0xE0FFFF);
        NAMED_COLORS.put("lightgoldenrodyellow", 0xFAFAD2);
        NAMED_COLORS.put("lightgray", 0xD3D3D3);
        NAMED_COLORS.put("lightgreen", 0x90EE90);
        NAMED_COLORS.put("lightgrey", 0xD3D3D3);
        NAMED_COLORS.put("lightpink", 0xFFB6C1);
        NAMED_COLORS.put("lightsalmon", 0xFFA07A);
        NAMED_COLORS.put("lightseagreen", 0x20B2AA);
        NAMED_COLORS.put("lightskyblue", 0x87CEFA);
        NAMED_COLORS.put("lightslategray", 0x778899);
        NAMED_COLORS.put("lightslategrey", 0x778899);
        NAMED_COLORS.put("lightsteelblue", 0xB0C4DE);
        NAMED_COLORS.put("lightyellow", 0xFFFFE0);
        NAMED_COLORS.put("lime", 0x00FF00);
        NAMED_COLORS.put("limegreen", 0x32CD32);
        NAMED_COLORS.put("linen", 0xFAF0E6);
        NAMED_COLORS.put("magenta", 0xFF00FF);
        NAMED_COLORS.put("maroon", 0x800000);
        NAMED_COLORS.put("mediumaquamarine", 0x66CDAA);
        NAMED_COLORS.put("mediumblue", 0x0000CD);
        NAMED_COLORS.put("mediumorchid", 0xBA55D3);
        NAMED_COLORS.put("mediumpurple", 0x9370DB);
        NAMED_COLORS.put("mediumseagreen", 0x3CB371);
        NAMED_COLORS.put("mediumslateblue", 0x7B68EE);
        NAMED_COLORS.put("mediumspringgreen", 0x00FA9A);
        NAMED_COLORS.put("mediumturquoise", 0x48D1CC);
        NAMED_COLORS.put("mediumvioletred", 0xC71585);
        NAMED_COLORS.put("midnightblue", 0x191970);
        NAMED_COLORS.put("mintcream", 0xF5FFFA);
        NAMED_COLORS.put("mistyrose", 0xFFE4E1);
        NAMED_COLORS.put("moccasin", 0xFFE4B5);
        NAMED_COLORS.put("navajowhite", 0xFFDEAD);
        NAMED_COLORS.put("navy", 0x000080);
        NAMED_COLORS.put("oldlace", 0xFDF5E6);
        NAMED_COLORS.put("olive", 0x808000);
        NAMED_COLORS.put("olivedrab", 0x6B8E23);
        NAMED_COLORS.put("orange", 0xFFA500);
        NAMED_COLORS.put("orangered", 0xFF4500);
        NAMED_COLORS.put("orchid", 0xDA70D6);
        NAMED_COLORS.put("palegoldenrod", 0xEEE8AA);
        NAMED_COLORS.put("palegreen", 0x98FB98);
        NAMED_COLORS.put("paleturquoise", 0xAFEEEE);
        NAMED_COLORS.put("palevioletred", 0xDB7093);
        NAMED_COLORS.put("papayawhip", 0xFFEFD5);
        NAMED_COLORS.put("peachpuff", 0xFFDAB9);
        NAMED_COLORS.put("peru", 0xCD853F);
        NAMED_COLORS.put("pink", 0xFFC0CB);
        NAMED_COLORS.put("plum", 0xDDA0DD);
        NAMED_COLORS.put("powderblue", 0xB0E0E6);
        NAMED_COLORS.put("purple", 0x800080);
        NAMED_COLORS.put("rebeccapurple", 0x663399);
        NAMED_COLORS.put("red", 0xFF0000);
        NAMED_COLORS.put("rosybrown", 0xBC8F8F);
        NAMED_COLORS.put("royalblue", 0x4169E1);
        NAMED_COLORS.put("saddlebrown", 0x8B4513);
        NAMED_COLORS.put("salmon", 0xFA8072);
        NAMED_COLORS.put("sandybrown", 0xF4A460);
        NAMED_COLORS.put("seagreen", 0x2E8B57);
        NAMED_COLORS.put("seashell", 0xFFF5EE);
        NAMED_COLORS.put("sienna", 0xA0522D);
        NAMED_COLORS.put("silver", 0xC0C0C0);
        NAMED_COLORS.put("skyblue", 0x87CEEB);
        NAMED_COLORS.put("slateblue", 0x6A5ACD);
        NAMED_COLORS.put("slategray", 0x708090);
        NAMED_COLORS.put("slategrey", 0x708090);
        NAMED_COLORS.put("snow", 0xFFFAFA);
        NAMED_COLORS.put("springgreen", 0x00FF7F);
        NAMED_COLORS.put("steelblue", 0x4682B4);
        NAMED_COLORS.put("tan", 0xD2B48C);
        NAMED_COLORS.put("teal", 0x008080);
        NAMED_COLORS.put("thistle", 0xD8BFD8);
        NAMED_COLORS.put("tomato", 0xFF6347);
        NAMED_COLORS.put("turquoise", 0x40E0D0);
        NAMED_COLORS.put("violet", 0xEE82EE);
        NAMED_COLORS.put("wheat", 0xF5DEB3);
        NAMED_COLORS.put("white", 0xFFFFFF);
        NAMED_COLORS.put("whitesmoke", 0xF5F5F5);
        NAMED_COLORS.put("yellow", 0xFFFF00);
        NAMED_COLORS.put("yellowgreen", 0x9ACD32);
    }

    public static boolean isValid(String name) {
        return NAMED_COLORS.containsKey(name);
    }

    /**
     * Resolves a CSS color name to its java.awt.Color. Returns black
     * if the name is not recognized (should not happen if isValid was
     * checked first).
     */
    public static Color toColor(String name) {
        Integer rgb = NAMED_COLORS.get(name);
        return rgb == null ? Color.black : new Color(rgb);
    }
}