package ca.ulaval.glo2004.util.parsing.tokenizer;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * Turns a string into a list of tokens to be able to parse after.
 */
public class Tokenizer {
    /**
     * The string to tokenize.
     */
    String source;

    /**
     * The current index in the string.
     */
    int currentIndex;

    /**
     * constrctor
     * @param toTokenize : the string to tokenize
     */
    public Tokenizer(String toTokenize) {
        this.source = toTokenize;
        this.currentIndex = 0;
    }

    /**
     * Turns the string into a list of tokens.
     * @return the list of tokens
     */
    public List<Token> tokenize() {
        List<Token> tokens = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();

        while (this.peek().isPresent())
        {
            Character c = this.consume();

            if(c.equals('\''))
            {
                tokens.add(new Token(TokenType.APOSTROPHE, String.valueOf(c)));
            }
            else if(c.equals('\"'))
            {
                tokens.add(new Token(TokenType.QUOTE, String.valueOf(c)));
            }
            else if(c.equals('/'))
            {
                tokens.add(new Token(TokenType.SLASH, String.valueOf(c)));
            }
            else if(Character.isDigit(c))
            {
                buffer.append(c);

                while(this.peek().isPresent() && Character.isDigit(this.peek().get()))
                {
                    buffer.append(this.consume());
                }

                tokens.add(new Token(TokenType.INT_LITERAL, buffer.toString()));
                buffer.setLength(0);
            }
            else if(Character.isSpaceChar(c))
            {
                // do nothing
            }
            else
            {
                throw new IllegalArgumentException("Invalid character in string: " + c);
            }
        }

        return tokens;
    }


    /**
     * Returns the character at the current index and increments the index.
     *
     * @return the character at the current index
     */
    private Character consume() {
        return this.source.charAt(currentIndex++);
    }

    /**
     * Returns the character at the current index without incrementing the index.
     * If the index is out of bounds, returns an empty optional.
     *
     * @return the character at the current index, or an empty optional if the index is out of bounds
     */
    private Optional<Character> peek()
    {
        if(this.currentIndex < this.source.length())
        {
            return Optional.of(this.source.charAt(this.currentIndex));
        }

        return Optional.empty();
    }
}
