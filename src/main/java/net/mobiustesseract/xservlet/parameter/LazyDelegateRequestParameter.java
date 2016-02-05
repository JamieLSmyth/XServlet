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

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 *
 * @param <T> actual type of parameter values
 *
 * @author Jamie L. Smyth MrJamieLSmyth@gmail.com
 */
public class LazyDelegateRequestParameter<T> extends BaseRequestParameter<T>
{

    private final HttpServletRequest                request;
    private final RequestParameter.ParameterType    type;

    protected LazyDelegateRequestParameter(
            String                          name,
            HttpServletRequest              request,
            RequestParameter.ParameterType  type
    )
    {
        super(name);
        this.request = request;
        this.type = type;
    }

    protected HttpServletRequest getRequest()
    {
        return this.request;
    }

    @Override
    public List<T> getValues()
    {
        return this.type.getDefaultLazyValuesProvider().apply(this.getName(), request).stream()
                    /* TODO this should do the actual type mapping as this will break on anything other than strings currently*/
                .map(v -> (T) v)
                .collect(Collectors.toList());
    }

}
