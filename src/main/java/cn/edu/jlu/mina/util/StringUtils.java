package cn.edu.jlu.mina.util;

public class StringUtils {

    /**
     * 除去字符串头尾部的指定字符，如果字符串是 <code>null</code> ，依然返回 <code>null</code>。
     * <p/>
     * <pre>
     *
     *    StringUtils.trim(null, *)                    = null
     *    StringUtils.trim(&quot;&quot;, *)            = &quot;&quot;
     *    StringUtils.trim(&quot;abc&quot;, null)      = &quot;abc&quot;
     *    StringUtils.trim(&quot;  abc&quot;, null)    = &quot;abc&quot;
     *    StringUtils.trim(&quot;abc  &quot;, null)    = &quot;abc&quot;
     *    StringUtils.trim(&quot; abc &quot;, null)    = &quot;abc&quot;
     *    StringUtils.trim(&quot;  abcyx&quot;, &quot;xyz&quot;) = &quot;  abc&quot;
     *
     * </pre>
     *
     * @param str        要处理的字符串
     * @param stripChars 要除去的字符，如果为 <code>null</code> 表示除去空白字符
     * @param mode       <code>-1</code> 表示trimStart， <code>0</code> 表示trim全部，
     *                   <code>1</code> 表示trimEnd
     * @return 除去指定字符后的的字符串，如果原字串为 <code>null</code> ，则返回 <code>null</code>
     */
    private static String trim(String str, String stripChars, int mode) {
        if (str == null) {
            return null;
        }
        int length = str.length();
        int start = 0;
        int end = length;
        // 扫描字符串头部
        if (mode <= 0) {
            if (stripChars == null) {
                while ((start < end)
                        && (Character.isWhitespace(str.charAt(start)))) {
                    start++;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end)
                        && (stripChars.indexOf(str.charAt(start)) != -1)) {
                    start++;
                }
            }
        }
        // 扫描字符串尾部
        if (mode >= 0) {
            if (stripChars == null) {
                while ((start < end)
                        && (Character.isWhitespace(str.charAt(end - 1)))) {
                    end--;
                }
            } else if (stripChars.length() == 0) {
                return str;
            } else {
                while ((start < end)
                        && (stripChars.indexOf(str.charAt(end - 1)) != -1)) {
                    end--;
                }
            }
        }
        if ((start > 0) || (end < length)) {
            return str.substring(start, end);
        }
        return str;
    }

    public static String trimStart(String str, String stripChars) {
        return trim(str, stripChars, -1);
    }
}
