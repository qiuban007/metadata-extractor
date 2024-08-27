package com.drew.metadata.flir;

import com.drew.lang.annotations.NotNull;
import com.drew.metadata.Directory;
import com.drew.metadata.TagDescriptor;

import java.util.HashMap;

public class FlirHeaderDirectory extends Directory {

    public static final int TAG_CREATOR_SOFTWARE = 0;
    @NotNull
    private static final HashMap<Integer, String> _tagNameMap = new HashMap<Integer, String>();

    static {
        _tagNameMap.put(TAG_CREATOR_SOFTWARE, "Creator Software");
    }

    public FlirHeaderDirectory()
    {
        setDescriptor(new TagDescriptor<FlirHeaderDirectory>(this));
    }


    @Override
    @NotNull
    public String getName() {
        return "FLIR Header";
    }

    @Override
    protected HashMap<Integer, String> getTagNameMap() {
        return _tagNameMap;
    }
}
