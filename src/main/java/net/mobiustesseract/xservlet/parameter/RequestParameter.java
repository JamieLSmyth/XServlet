/**
 * Copyright (c) 2016 Jamie L. Smyth
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction,
 * including without limitation the rights to use, copy, modify, merge, publish, distribute,
 * sublicense, and/or sell copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT
 * NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES
 * OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN
 * CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package net.mobiustesseract.xservlet.parameter;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.net.URLDecoder;
import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A type mapped name/values pair for a parameter in a Servlet (i.e. header, query, cookie, matrix, form)
 * @param <T> actual type of parameter values
 *
 * @author Jamie L. Smyth MrJamieLSmyth@gmail.com
 */
public interface RequestParameter<T>
{
    public static enum ParameterType
    {
        QUERY(QUERY_VALUE_PROVIDER),
        HEADER(HEADER_VALUE_PROVIDER),
        COOKIE(COOKIE_VALUE_PROVIDER)
        //TODO implement MATRIX
        //TODO implement FORM
        ;

        private final BiFunction<String, HttpServletRequest, List<String>> defaultLazyValuesProvider;

        private ParameterType(BiFunction<String, HttpServletRequest, List<String>> defaultLazyValuesProvider)
        {
            this.defaultLazyValuesProvider = defaultLazyValuesProvider;
        }

        public BiFunction<String, HttpServletRequest, List<String>> getDefaultLazyValuesProvider()
        {
            return this.defaultLazyValuesProvider;
        }
    }

    public String getName();

    public List<T> getValues();

    public default List<T> getValues(boolean allowNull)
    {
        return this.getValues().stream().filter(Objects::nonNull).collect(Collectors.toList());
    }

    public default boolean exists()
    {
        return !this.getValues().isEmpty();
    }

    //TODO Move these out of this interface as they do not need to be public
    public static final BiFunction<String, HttpServletRequest, List<String>> QUERY_VALUE_PROVIDER  = RequestParameter::getQueryParameterValues;
    public static final BiFunction<String, HttpServletRequest, List<String>> HEADER_VALUE_PROVIDER = (
            name,
            request
    ) -> {
        Enumeration<String> value = request.getHeaders(name);
        return value == null ? Collections.emptyList() : Collections.list(value)
                                                                    .stream()
                                                                    .collect(Collectors.toList());
    };
    public static final BiFunction<String, HttpServletRequest, List<String>> COOKIE_VALUE_PROVIDER = (
            name,
            request
    ) -> {
        Cookie[] value = request.getCookies();
        return value == null ? Collections.emptyList() : Stream.of(value)
                                                               .filter(cookie -> cookie.getName().equals(name))
                                                               .map(Cookie::getValue)
                                                               .collect(Collectors.toList());
    };

    public static List<String> getQueryParameterValues(String name, HttpServletRequest request)
    {
        List<String> values = new LinkedList<>();
        String queryString = request.getQueryString();

        if (queryString == null)
        {
            return Collections.emptyList();
        }
        StringTokenizer st = new StringTokenizer(queryString, "&");
        int i;

        while (st.hasMoreTokens())
        {
            String s = st.nextToken();
            i = s.indexOf("=");
            if (i > 0)
            {
                String paramname = s.substring(0, i);
                String value = s.substring(i + 1);

                try
                {
                    paramname = URLDecoder.decode(paramname, "UTF-8");
                }
                catch (Exception e)
                {
                }
                try
                {
                    value = URLDecoder.decode(value, "UTF-8");
                }
                catch (Exception e)
                {
                }

                if (paramname.equals(name))
                {
                    values.add(value);
                }
            }
            else
            {
                values.add(null);
            }
        }

        return values;
    }
}
