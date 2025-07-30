package com.cxj.utility;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Created by 陈晓靖 on 2018/4/18 17:53
 */
public interface TypeCaster {

    @Nullable
    Object cast(@Nonnull Object input);

    @Nullable
    Class getInputType();

    @Nonnull
    Class getOutputType();

    boolean isTerminal();
}
