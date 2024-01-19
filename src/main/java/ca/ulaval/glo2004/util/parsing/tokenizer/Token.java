package ca.ulaval.glo2004.util.parsing.tokenizer;

import java.util.Optional;

/**
 * Represents a token for the tokenizer and parser.
 */
public class Token {
    /**
     * The type of the token.
     */
    TokenType type;

    /**
     * The value of the token.
     */
    Optional<String> value;

    /**
     * constructor
     * @param type_ : the type of the token
     */
    public Token(TokenType type_){
        this.type = type_;
        this.value = Optional.empty();
    }

    /**
     * constructor
     * @param type_ : the type of the token
     * @param value_ : the value of the token
     */
    public Token(TokenType type_, String value_)
    {
        this.type = type_;
        this.value = Optional.of(value_);
    }

    /**
     * type getter
     * @return the type of the token
     */
    public TokenType getType() {
        return type;
    }

    /**
     * value getter
     * @return the value of the token
     */
    public Optional<String> getValue() {
        return value;
    }
}
