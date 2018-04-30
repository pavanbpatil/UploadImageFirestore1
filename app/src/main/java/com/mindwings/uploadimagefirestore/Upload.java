package com.mindwings.uploadimagefirestore;

import java.security.PublicKey;

/**
 * Created by mindwings on 4/23/2018.
 */

public class Upload {
    private String mName;
    private String mImage;

    public Upload()
    {}
    public Upload(String nm, String img)
    {
        if(nm.trim().equals(""))
        {
            nm="No Name";
        }
        mName=nm;
        mImage=img;
    }
    public String getName() {
        return mName;
    }

    public void setName(String nm) {
        mName = nm;
    }

    public String getImage() {
        return mImage;
    }

    public void setImage(String img) {
        mImage = img;
    }
}
