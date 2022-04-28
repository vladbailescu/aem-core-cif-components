/*~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~
 ~ Copyright 2022 Adobe
 ~
 ~ Licensed under the Apache License, Version 2.0 (the "License");
 ~ you may not use this file except in compliance with the License.
 ~ You may obtain a copy of the License at
 ~
 ~     http://www.apache.org/licenses/LICENSE-2.0
 ~
 ~ Unless required by applicable law or agreed to in writing, software
 ~ distributed under the License is distributed on an "AS IS" BASIS,
 ~ WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ~ See the License for the specific language governing permissions and
 ~ limitations under the License.
 ~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~*/
package com.adobe.cq.commerce.core.components.internal.servlets;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.Servlet;

import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.component.annotations.ReferenceCardinality;
import org.osgi.service.component.annotations.ReferencePolicy;
import org.osgi.service.component.annotations.ReferencePolicyOption;

import com.adobe.cq.commerce.core.components.internal.services.UrlProviderImpl;
import com.adobe.cq.commerce.core.components.services.urls.CategoryUrlFormat;
import com.adobe.cq.commerce.core.components.services.urls.UrlFormat;
import com.adobe.granite.ui.components.ds.DataSource;

@Component(
    service = { Servlet.class },
    property = {
        "sling.servlet.resourceTypes=" + CategoryPageUrlFormatsServlet.RESOURCE_TYPE,
        "sling.servlet.methods=GET",
        "sling.servlet.extensions=html"
    })
public class CategoryPageUrlFormatsServlet extends AbstractPageUrlFormatsServlet {
    static final String RESOURCE_TYPE = "core/cif/components/datasource/categoryurlformats";
    static final String PAGE_URL_FORMAT_KEY = "categoryPageUrlFormat";
    static final List<String> URL_FORMATS = UrlProviderImpl.DEFAULT_CATEGORY_URL_FORMATS.keySet().stream()
        .map(f -> f.replace("#", "\\#"))
        .collect(Collectors.toList());

    @Reference(
        cardinality = ReferenceCardinality.MULTIPLE,
        policy = ReferencePolicy.STATIC,
        policyOption = ReferencePolicyOption.GREEDY,
        target = "("
            + UrlFormat.PROP_USE_AS + "=" + UrlFormat.CATEGORY_PAGE_URL_FORMAT + ")")
    private List<UrlFormat> categoryPageUrlFormats;

    @Reference(cardinality = ReferenceCardinality.MULTIPLE, policy = ReferencePolicy.STATIC, policyOption = ReferencePolicyOption.GREEDY)
    private List<CategoryUrlFormat> newCategoryUrlFormat;

    @Override
    protected void doGet(SlingHttpServletRequest request, SlingHttpServletResponse response) {
        List<String> customFormatNames = new ArrayList<>();

        if (newCategoryUrlFormat != null) {
            newCategoryUrlFormat.forEach(f -> customFormatNames.add(f.getClass().getName()));
        }

        if (categoryPageUrlFormats != null) {
            categoryPageUrlFormats.forEach(f -> customFormatNames.add(f.getClass().getName()));
        }

        request.setAttribute(DataSource.class.getName(), getDataSource(request,
            URL_FORMATS,
            customFormatNames,
            PAGE_URL_FORMAT_KEY));
    }
}
