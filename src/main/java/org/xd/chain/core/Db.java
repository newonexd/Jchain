package org.xd.chain.core;

import org.codehaus.jackson.annotate.JsonProperty;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Db {
    @JsonProperty("_id")
    protected String id;
    @JsonProperty("_rev")
    protected String rev;
}