package com.sbor.tool;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Ingredient {

    public String name;

    @JsonProperty("count")
    public int quantity; // maps JSON "count" → quantity
}
