package com.aroq.druflow

class Branch {
    String name

    Branch parent

    boolean autoMergeToParent = false

    // private def children

    String getParentName() { parent?.name }
}
