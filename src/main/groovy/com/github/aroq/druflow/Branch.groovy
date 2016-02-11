package com.github.aroq.druflow

class Branch {
    String name

    Branch parent

    boolean autoMergeToParent = false

    String getParentName() { parent?.name }
}
