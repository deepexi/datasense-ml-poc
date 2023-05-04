package com.deepexi.ds.ast.visitor.generator;

import com.deepexi.ds.ModelException.UnsupportedException;

/**
 * 是否用 `` '' 来将字段包裹起来
 */
public interface IdentifierQuotePolicy {

  boolean hasQuote();

  String quote();

  IdentifierQuotePolicy NO_QUOTE = new IdentifierPolicyNoQuote();
  IdentifierQuotePolicy BACK_TICK = new IdentifierPolicyBackTick();

  final class IdentifierPolicyNoQuote implements IdentifierQuotePolicy {

    @Override
    public boolean hasQuote() {
      return false;
    }

    @Override
    public String quote() {
      throw new UnsupportedException("this method should NOT be called, since hasQuote is false");
    }
  }

  final class IdentifierPolicyBackTick implements IdentifierQuotePolicy {

    @Override
    public boolean hasQuote() {
      return true;
    }

    @Override
    public String quote() {
      return "`";
    }
  }
}
