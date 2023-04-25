package com.deepexi.ds.ast.expression;

import com.deepexi.ds.ModelException;
import com.deepexi.ds.ModelException.UnsupportedException;

/**
 * 是否用 `` '' 来将字段包裹起来
 */
public interface IdentifierPolicy {

  boolean hasQuote();

  String quoteString();


  public static final class IdentifierPolicyNoQuote implements IdentifierPolicy {

    public static final IdentifierPolicyNoQuote INSTANCE = new IdentifierPolicyNoQuote();

    @Override
    public boolean hasQuote() {
      return false;
    }

    @Override
    public String quoteString() {
      throw new UnsupportedException("this method should NOT be called, since hasQuote is false");
    }
  }

  public static final class IdentifierPolicyBackTick implements IdentifierPolicy {

    public static final IdentifierPolicyBackTick INSTANCE = new IdentifierPolicyBackTick();

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
