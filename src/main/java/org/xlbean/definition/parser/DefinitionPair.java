package org.xlbean.definition.parser;

/**
 * Pair of DefinitionUnit.
 * 
 * @author tanikawa
 *
 */
public class DefinitionPair {

    private DefinitionUnit left;
    private DefinitionUnit right;

    public DefinitionPair(DefinitionUnit left, DefinitionUnit right) {
        this.left = left;
        this.right = right;
    }

    public DefinitionUnit getLeft() {
        return left;
    }

    public void setLeft(DefinitionUnit left) {
        this.left = left;
    }

    public DefinitionUnit getRight() {
        return right;
    }

    public void setRight(DefinitionUnit right) {
        this.right = right;
    }

    @Override
    public String toString() {
        return "DefinitionPair [left=" + left + ", right=" + right + "]";
    }

}
