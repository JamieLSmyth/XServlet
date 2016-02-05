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
package net.mobiustesseract.xservlet.http;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.Collections;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import net.mobiustesseract.xservlet.parameter.AggregateRequestParameter;
import net.mobiustesseract.xservlet.parameter.LazyDelegateAggregateRequestParameter;
import net.mobiustesseract.xservlet.parameter.RequestParameter;

/**
 * <p>An extension to the HttpServletRequest.</p>
 * <p>Adds the following features:
 * <ul>
 *     <li>Standardization of parameters (i.e. header, query, form, matrix, cookie)</li>
 *     <li>Aggregate parameters which aggregates values from all parameter types with the specified name</li>
 *     <li>Prioritized parameters which searches for parameters from all types in a specified order</li>
 * </ul>
 * </p>
 * @author Jamie L. Smyth MrJamieLSmyth@gmail.com
 */
public interface XHttpServletRequest extends HttpServletRequest
{

    default Set<String> getAggregateParameterNames()
    {
        return Stream.of(
                Collections.list(this.getParameterNames()).stream(),
                Collections.list(this.getHeaderNames()).stream(),
                Optional.ofNullable(this.getCookies())
                        .map(cookies -> Stream.of(cookies).map(Cookie::getName))
                        .orElse(Stream.empty())
                //TODO add matrix parameter names
        )
                     .flatMap(Function.identity())
                     .collect(Collectors.toSet());
    }

    default AggregateRequestParameter<String> getAggregateRequestParameter(String name)
    {
        return new LazyDelegateAggregateRequestParameter<>(name, this);
    }
    default Set<AggregateRequestParameter<String>> getAggregateRequestParameters()
    {
        return this.getAggregateParameterNames().stream()
                   .map(name -> new LazyDelegateAggregateRequestParameter<String>(name, this))
                   .collect(Collectors.toSet());
    }

    default RequestParameter<String> getQueryParameter(String name)
    {
        return this.getAggregateRequestParameter(name).getQueryParameter();
    }

    default Set<RequestParameter<String>> getQueryParameters()
    {
        return this.getAggregateRequestParameters().stream()
                   .map(composite -> composite.getQueryParameter())
                .filter(RequestParameter::exists)
                .collect(Collectors.toSet());
    }

    default RequestParameter<String> getHeaderParameter(String name)
    {
        return this.getAggregateRequestParameter(name).getHeaderParameter();
    }

    default Set<RequestParameter<String>> getHeaderParameters()
    {
        return this.getAggregateRequestParameters().stream()
                   .map(AggregateRequestParameter::getHeaderParameter)
                   .filter(RequestParameter::exists)
                   .collect(Collectors.toSet());
    }

    default RequestParameter<String> getCookieParameter(String name)
    {
        return this.getAggregateRequestParameter(name).getCookieParameter();
    }

    default Set<RequestParameter<String>> getCookieParameters()
    {
        return this.getAggregateRequestParameters().stream()
                   .map(AggregateRequestParameter::getCookieParameter)
                   .filter(RequestParameter::exists)
                   .collect(Collectors.toSet());
    }

    //TODO implement matrix parameters
    //TODO implement form parameters

}
