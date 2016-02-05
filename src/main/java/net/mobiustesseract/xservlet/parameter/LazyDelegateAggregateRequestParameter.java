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
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 *
 * @param <T> actual type of parameter values
 * @author Jamie L. Smyth MrJamieLSmyth@gmail.com
 */
public class LazyDelegateAggregateRequestParameter<T> extends BaseRequestParameter<T> implements AggregateRequestParameter<T>
{
    private final RequestParameter<T> queryParameter;
    private final RequestParameter<T> cookieParameter;
    private final RequestParameter<T> headerParameter;
    //TODO implement matrix
    //TODO implement form

    public LazyDelegateAggregateRequestParameter(String name, HttpServletRequest request)
    {
        super(name);
        this.queryParameter = new LazyDelegateRequestParameter<>(name, request, ParameterType.QUERY);
        this.cookieParameter = new LazyDelegateRequestParameter<>(name, request, ParameterType.COOKIE);
        this.headerParameter = new LazyDelegateRequestParameter<>(name, request, ParameterType.HEADER);
    }

    @Override
    public RequestParameter<T> getCookieParameter()
    {
        return this.cookieParameter;
    }

    @Override
    public RequestParameter<T> getQueryParameter()
    {
        return this.queryParameter;
    }

    @Override
    public RequestParameter<T> getHeaderParameter()
    {
        return this.headerParameter;
    }


    @Override
    public List<T> getValues()
    {
        return Stream.of(
                this.getQueryParameter().getValues().stream(),
                this.getCookieParameter().getValues().stream(),
                this.getHeaderParameter().getValues().stream()
        )
                     .flatMap(Function.identity())
                     .collect(Collectors.toList());
    }
}
