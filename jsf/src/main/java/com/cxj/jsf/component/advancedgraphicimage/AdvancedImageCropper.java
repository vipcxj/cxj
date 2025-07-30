/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.component.advancedgraphicimage;

import javax.faces.application.ResourceDependencies;
import javax.faces.application.ResourceDependency;
import javax.faces.component.FacesComponent;
import org.primefaces.component.imagecropper.ImageCropper;
import org.primefaces.model.StreamedContent;

/**
 *
 * @author Administrator
 */
@ResourceDependencies({
    @ResourceDependency(library = "primefaces", name = "components.css"),
    @ResourceDependency(library = "primefaces", name = "imagecropper/imagecropper.css"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery.js"),
    @ResourceDependency(library = "primefaces", name = "jquery/jquery-plugins.js"),
    @ResourceDependency(library = "primefaces", name = "core.js"),
    @ResourceDependency(library = "primefaces", name = "components.js"),
    @ResourceDependency(library = "primefaces", name = "imagecropper/imagecropper.js")
})
@FacesComponent(AdvancedImageCropper.COMPONENT_TYPE)
public class AdvancedImageCropper extends ImageCropper {

    @SuppressWarnings("FieldNameHidesFieldInSuperclass")
    public static final String COMPONENT_TYPE = "com.cxj.jsf.component.advancedgraphicimage.AdvancedImageCropper";
    public static final String RENDER_TYPE = "com.cxj.jsf.component.advancedgraphicimage.AdvancedImageCropperRender";

    protected enum PropertyKeys {

        imageValue;

        String toString;

        PropertyKeys(String toString) {
            this.toString = toString;
        }

        PropertyKeys() {
        }

        @Override
        public String toString() {
            return ((this.toString != null) ? this.toString : super.toString());
        }
    }

    public AdvancedImageCropper() {
        setRendererType(RENDER_TYPE);
    }

    public StreamedContent getImageValue() {
        return (StreamedContent) getStateHelper().eval(PropertyKeys.imageValue);
    }

    public void setImageValue(StreamedContent image) {
        getStateHelper().put(PropertyKeys.imageValue, image);
    }
}
