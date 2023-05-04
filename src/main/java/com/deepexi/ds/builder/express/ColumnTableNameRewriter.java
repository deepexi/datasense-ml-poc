package com.deepexi.ds.builder.express;

import static java.util.Collections.EMPTY_MAP;

import com.deepexi.ds.ast.AstNode;
import com.deepexi.ds.ast.expression.Identifier;
import com.google.common.collect.ImmutableMap;
import java.util.HashMap;
import java.util.Map;

/**
 * <pre>
 * colA =>
 * tableX.colA
 * </pre>
 */
public class ColumnTableNameRewriter extends BaseColumnIdentifierRewriter {

  private final Identifier fromRelation;

  private final ImmutableMap<Identifier, Identifier> replaceMap;

  public ColumnTableNameRewriter(Identifier identifier) {
    this(identifier, EMPTY_MAP);
  }

  public ColumnTableNameRewriter(Identifier identifier,
      Map<Identifier, Identifier> replaceMap) {
    this.fromRelation = identifier;
    this.replaceMap = ImmutableMap.copyOf(replaceMap);
  }

  public ColumnTableNameRewriter(Identifier identifier, Identifier fromTable, Identifier toTable) {
    this.fromRelation = identifier;
    Map<Identifier, Identifier> tmp = new HashMap<>();
    tmp.put(fromTable, toTable);
    this.replaceMap = ImmutableMap.copyOf(tmp);
  }

  @Override
  public AstNode visitIdentifier(Identifier node, Void context) {
    if (node.getPrefix() != null) {
      Identifier srcTable = Identifier.of(node.getPrefix());
      Identifier replaceTo = replaceMap.get(srcTable);
      if (replaceTo != null) {
        return new Identifier(replaceTo.getValue(), node.getValue());
      } else {
        return node;
      }
    }
    String prefix = fromRelation.getValue();
    return new Identifier(prefix, node.getValue());
  }
}
