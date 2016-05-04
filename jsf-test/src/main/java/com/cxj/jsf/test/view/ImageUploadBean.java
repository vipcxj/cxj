/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cxj.jsf.test.view;

import com.cxj.jsf.test.view.config.Pages;
import java.io.IOException;
import java.io.Serializable;
import javax.inject.Named;
import org.omnifaces.cdi.ViewScoped;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;

/**
 *
 * @author cxj
 */
@Named
@ViewScoped
public class ImageUploadBean implements Serializable {

    private static final long serialVersionUID = -6794992107665395688L;

    private StreamedContent image;

    public StreamedContent getImage() {
        return image;
    }

    public void setImage(StreamedContent image) {
        this.image = image;
    }

    public void onImageUpload(FileUploadEvent evt) throws IOException {
        if (evt != null && evt.getFile() != null) {
            UploadedFile file = evt.getFile();
            image = new DefaultStreamedContent(file.getInputstream(), file.getContentType());
        }
    }
    
    public Class<Pages.Welcome> back() {
        return Pages.Welcome.class;
    }

}
