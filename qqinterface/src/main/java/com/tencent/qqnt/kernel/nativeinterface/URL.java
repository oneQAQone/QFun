package com.tencent.qqnt.kernel.nativeinterface;

import java.io.Serializable;

public final class URL implements Serializable {
    public Specification spec = Specification.values()[0];
    public String url = "";
    long serialVersionUID = 1;

    public Specification getSpec() {
        return this.spec;
    }

    public void setSpec(Specification specification) {
        this.spec = specification;
    }

    public String getUrl() {
        return this.url;
    }

    public void setUrl(String str) {
        this.url = str;
    }
}
