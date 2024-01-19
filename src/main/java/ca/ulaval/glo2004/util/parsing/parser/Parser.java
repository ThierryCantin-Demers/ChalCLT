package ca.ulaval.glo2004.util.parsing.parser;

import ca.ulaval.glo2004.util.parsing.tokenizer.Token;
import ca.ulaval.glo2004.util.parsing.tokenizer.TokenType;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Parses a list of tokens into a list of nodes.
 */
public class Parser {
    /**
     * The list of tokens to parse.
     */
    final List<Token> tokens;

    /**
     * The current index in the list of tokens.
     */
    int currentIndex;

    /**
     * constructor
     * @param tokens_ : the list of tokens to parse
     */
    public Parser(List<Token> tokens_)
    {
        this.tokens = tokens_;
    }

    /**
     * Parses the list of tokens into a list of nodes.
     * @return the list of nodes
     */
    public List<Node> parse()
    {
        List<Node> nodes = new ArrayList<>();

        while(this.peek().isPresent())
        {
            Token t = consume();

            if(t.getType().equals(TokenType.INT_LITERAL))
            {
                if(!t.getValue().isPresent())
                {
                    throw new IllegalArgumentException("Null optional in INT_LITERAL value");
                }

                if(peek().isPresent() && peek().get().getType().equals(TokenType.APOSTROPHE))
                {
                    consume();
                    nodes.add(new NodeFeet(Integer.parseInt(t.getValue().get())));
                }
                else if(peek().isPresent() && peek().get().getType().equals(TokenType.QUOTE))
                {
                    consume();
                    nodes.add(new NodeInches(Integer.parseInt(t.getValue().get())));
                }
                else if(peek().isPresent() && peek().get().getType().equals(TokenType.SLASH)&&
                peek(1).isPresent() && peek(1).get().getType().equals(TokenType.INT_LITERAL))
                {
                    consume();
                    Token t2 = consume();
                    if(!t2.getValue().isPresent())
                    {
                        throw new IllegalArgumentException("Null optional in INT_LITERAL value");
                    }
                    else if(Integer.parseInt(t2.getValue().get()) == 0)
                    {
                        throw new IllegalArgumentException("Denominator cannot be 0");
                    }

                    nodes.add(new NodeFraction(Integer.parseInt(t.getValue().get()), Integer.parseInt(t2.getValue().get())));
                }
                else {
                    throw new IllegalArgumentException("Invalid node parsing");
                }
            }
        }

        return nodes;
    }

    /**
     * Returns the token at the current index and increments the index.
     *
     * @return the token at the current index
     */
    private Token consume() {
        return this.tokens.get(currentIndex++);
    }

    /**
     * Returns the token at the current index without incrementing the index.
     * If the index is out of bounds, returns an empty optional.
     *
     * @param offset_ : the offset from the current index (used to peek further than the next token)
     * @return the token at the current index, or an empty optional if the index is out of bounds
     */
    private Optional<Token> peek(int offset_)
    {
        if(this.currentIndex + offset_ < this.tokens.size())
        {
            return Optional.of(this.tokens.get(this.currentIndex + offset_));
        }

        return Optional.empty();
    }


    /**
     * Peeks with an offset of 0.
     *
     * @return the token at the current index, or an empty optional if the index is out of bounds
     */
    Optional<Token> peek()
    {
        return this.peek(0);
    }
}
