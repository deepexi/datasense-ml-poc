package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ModelException.UnsupportedException;

/**
 * 是否用 `` '' 来将字段包裹起来
 */
public interface IdentifierPolicy {

  boolean hasQuote();

  String quoteString();

  IdentifierPolicy NO_QUOTE = new IdentifierPolicyNoQuote();
  IdentifierPolicy BACK_TICK = new IdentifierPolicyBackTick();

  final class IdentifierPolicyNoQuote implements IdentifierPolicy {

    @Override
    public boolean hasQuote() {
      return false;
    }

    @Override
    public String quoteString() {
      throw new UnsupportedException("this method should NOT be called, since hasQuote is false");
    }
  }

  final class IdentifierPolicyBackTick implements IdentifierPolicy {

    @Override
    public boolean hasQuote() {
      return true;
    }

    @Override
    public String quoteString() {
      return "`";
    }
  }
}
