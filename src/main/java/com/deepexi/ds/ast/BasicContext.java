package com.deepexi.ds.ast;

import java.util.Objects;
import lombok.Getter;

@Getter
public class BasicContext {

  protected final Model root;

  public BasicContext(Model root) {
    Objects.requireNonNull(root, "Model %s not found in QueryContext");
    this.root = root;
  }
}
