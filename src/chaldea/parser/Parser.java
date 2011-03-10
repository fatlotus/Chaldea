/* thingy
 */
package chaldea.parser;

import java.io.IOException;
import java.io.InputStream;

import java.util.LinkedList;
import java.util.Vector;
import java.util.Stack;

/**
 * @author Jeremy Archer
 * @version $Id$
 */
public abstract class Parser
{
	protected final static int ATOM = 256;
	protected final static int INTEGER = 257;
	protected final static int NEWLINE = 258;
	protected final static int UNKNOWN = 259;
	protected final static int SEMICOLON = 260;
	protected final static int STRING = 261;
	protected final static int IVAR = 262;
	protected final static int CVAR = 263;
	protected final static int OBRACKET = 264;
	protected final static int CBRACKET = 265;
	protected final static int OPAREN = 266;
	protected final static int CPAREN = 267;
	protected final static int COMMA = 268;
	protected final static int ASSIGN = 269;
	protected final static int PLUS = 270;
	protected final static int MINUS = 271;

	protected final static int INITIAL = 0;
	protected final static int STRING_STATE = 22;

	// an internal class for lazy initiation
	private final static class cc_lexer
	{
		private static char[] accept = ("\000\022\007\021\006\005\015\016\012\017\013\004\014\020\022\001\010\011\024\000\002\003\000\023\023\024\000\025").toCharArray ();
		private static char[] ecs = ("\000\000\000\000\000\000\000\000\000\001\002\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\001\000\003\004\000\000\000\000\005\006\000\007\010\011\000\000\012\012\012\012\012\012\012\012\012\012\000\013\000\014\000\000\015\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\000\000\000\000\017\000\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\016\020\000\021\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\022").toCharArray ();
		private static char[] base = ("\023\000\000\000\000\000\000\000\000\000\001\002\000\000\031\000\000\000\000\032\003\002\000\001\000\000\000\000\000\005\007\051").toCharArray ();
		private static char[] next = ("\005\002\005\030\033\027\027\032\032\000\017\013\025\024\017\017\000\013\031\001\002\003\004\005\006\007\010\011\012\013\014\015\016\017\017\020\021\022\023\024\025\000\000\000\000").toCharArray ();
		private static char[] check = ("\034\002\034\026\027\035\035\036\036\037\017\012\025\024\017\017\024\013\026\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\016\016\023\037\037\037\037").toCharArray ();
		private static char[] defaults = ("\037\037\001\037\037\034\037\037\037\037\001\012\037\037\001\001\037\037\037\001\016\023\035\036\037\037\027\037\037\037\037\037").toCharArray ();
		private static char[] meta = ("\000\000\001\002\000\000\000\000\000\000\000\000\000\000\000\000\000\000\003").toCharArray ();
	}

	// an internal class for lazy initiation
	private final static class cc_parser
	{
		private static char[] rule = ("\000\001\003\003\001\002\000\002\002\001\001\001\000\004\003\001\002\002\001\001\001\003\000\002\001\003\003\004\001\003\001\001\003\001\001\002\004\003\001\003\001\001\003\002\001\002\002\001\001\001\003").toCharArray ();
		private static char[] ecs = ("\000\001\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\002\003\004\000\005\006\007\000\010\011\012\013\014\015\016\017").toCharArray ();
		private static char[] base = ("\u00e2\005\u00e3\132\000\000\125\000\002\u00e8\043\u0092\002\004\126\u00e9\023\026\000\044\022\016\040\u00ec\101\001\043\070\u00f4\u00fc\u0104\150\072\054\u0103\053\u010e\u0116\u011e\156\167\u0080\u008a\u008c\020\057\112\u012a\144\055\u0092\u009b\u00a4\135\000\000\000\u00ad\060\151\u00af\000\000\u00b8\000\066\u00c1\u00ca\u00cc\u00d5\000\000\107\112\120\u0132\u0147\u013b\u0161\u0161\u013b\u0144\u0161\u0161\u0161\u0161\u0161\u0161\u0139\u0161\u013c\u0161\u0161\u0161\u0148\u0161\u0161\u0161\u0141\u0135\u0161\u0142\u0161\u0161\u0161\u0161\u0161\u0140\u0161\u0161\u0161\u0161\u0161\u0161\u0141\u0161\u0161\u0161\u0152\u0161\u0161\u0161\u0161\u0161\u0148\u014a\u0161\u0161\u0147\u0130\u0161\u014f\u014e\u014f\u0161\u0161\u0161\u0150\u0161\u0161\u0161\u0161\u0161\u0161\u0161\u0161\u0161\u0161").toCharArray ();
		private static char[] next = ("\ufffb\uffd4\ufffb\030\ufffb\uffff\ufffa\003\007\ufffb\uffef\ufffa\041\ufff1\ufffb\ufffb\ufffa\ufffa\ufff1\ufff1\uffe5\uffee\uffe4\ufff2\uffed\uffe5\027\uffe4\ufff2\ufff2\uffe5\uffe5\uffe4\uffe4\030\ufffe\uffea\ufffe\026\uffe6\uffea\uffea\031\uffe6\uffe6\056\uffea\uffea\uffe9\uffe6\uffe6\uffeb\uffe9\uffe9\000\uffeb\uffeb\uffe3\uffe9\uffe9\uffec\uffeb\uffeb\106\uffec\uffec\000\054\043\uffe8\uffec\uffec\uffd3\uffe8\uffe8\uffd2\uffe8\uffe8\uffe7\uffe8\uffe8\uffce\uffe7\uffe7\000\uffe7\uffe7\000\uffe7\uffe7\000\000\010\014\000\017\011\uffd0\uffd0\000\020\021\uffd0\000\uffd0\uffd0\046\047\057\000\050\051\000\uffe2\uffde\uffde\104\000\uffde\uffde\000\uffde\uffde\uffdf\uffdf\000\000\uffdf\uffdf\000\uffdf\uffdf\uffd8\uffd8\000\000\uffd8\uffd8\000\uffd8\uffd8\070\034\035\uffe1\uffe1\ufffd\ufff4\ufffd\uffe1\uffd7\uffd7\000\000\uffd7\uffd7\000\uffd7\uffd7\uffdd\uffdd\000\000\uffdd\uffdd\000\uffdd\uffdd\uffd1\uffd1\000\000\057\uffd1\000\uffd1\075\044\045\uffdb\uffdb\000\ufff5\uffdb\uffdb\000\uffdb\uffdb\uffd5\uffd5\000\000\uffd5\uffd5\000\uffd5\uffd5\uffd9\uffd9\000\000\uffd9\uffd9\000\uffd9\uffd9\uffe0\uffe0\uffdc\uffdc\000\uffe0\uffdc\uffdc\000\uffdc\uffdc\uffd6\uffd6\000\000\uffd6\uffd6\000\uffd6\uffd6\ufffa\ufffc\ufffa\ufffc\ufffa\007\ufff0\ufff3\ufff0\ufff3\ufff0\ufff3\ufff4\ufff4\034\035\ufff4\ufff4\000\ufff4\ufff7\ufff7\ufff7\ufff7\ufff7\ufff7\000\ufff7\ufff6\ufff6\ufff6\ufff6\ufff6\ufff6\000\ufff6\ufff5\ufff5\044\045\ufff5\ufff5\000\ufff5\055\043\ufff9\ufff9\ufff9\ufff9\ufff9\ufff9\000\ufff9\ufff8\ufff8\ufff8\ufff8\ufff8\ufff8\000\ufff8\046\047\uffda\uffda\050\051\057\uffda\060\uffda\uffda\061\uffcf\uffcf\uffcf\uffcf\uffcf\uffcf\000\000\000\000\000\000\000\000\036\037\015\004\012\005\077\016\006\040\100\022\023\013\001\024\002\025\032\033\042\052\053\062\063\064\065\066\067\071\072\073\074\076\067\101\102\103\105\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000\000").toCharArray ();
		private static char[] check = ("\007\107\007\031\007\001\014\001\015\007\010\014\031\015\007\007\014\014\015\015\054\020\024\025\021\054\024\024\025\025\054\054\024\024\026\012\026\012\023\032\026\026\026\032\032\043\026\026\041\032\032\055\041\041\061\055\055\072\041\041\033\055\055\101\033\033\040\040\033\030\033\033\110\030\030\111\030\030\056\030\030\112\056\056\113\056\056\006\056\056\003\006\003\006\016\016\003\065\065\113\016\016\065\113\065\065\037\037\060\060\037\037\113\037\047\047\073\073\047\047\113\047\047\050\050\113\113\050\050\113\050\050\051\051\113\113\051\051\113\051\051\051\052\052\053\053\013\052\013\053\062\062\113\113\062\062\113\062\062\063\063\113\113\063\063\113\063\063\064\064\113\113\064\064\113\064\064\071\071\074\074\113\071\074\074\113\074\074\077\077\113\113\077\077\113\077\077\102\102\113\113\102\102\113\102\102\103\103\104\104\113\103\104\104\113\104\104\105\105\113\113\105\105\113\105\105\000\002\000\002\000\002\011\017\011\017\011\017\027\027\027\027\027\027\113\027\034\034\034\034\034\034\113\034\035\035\035\035\035\035\113\035\036\036\036\036\036\036\042\036\042\042\044\044\044\044\044\044\113\044\045\045\045\045\045\045\113\045\046\046\046\046\046\046\046\046\046\046\046\046\057\057\057\057\057\057\113\113\113\113\113\113\113\113\143\143\130\115\120\115\u0081\130\115\143\u0081\132\132\121\114\132\114\136\142\142\145\153\153\162\162\162\162\162\162\166\166\174\175\u0080\u0080\u0083\u0084\u0085\u0089\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093").toCharArray ();
		private static char[] defaults = ("\113\113\000\001\000\000\003\000\006\000\002\002\113\014\006\000\113\020\014\020\006\015\006\003\006\020\006\006\003\003\003\003\006\006\040\020\003\003\113\006\006\006\040\040\014\006\006\003\037\037\006\006\006\060\107\027\061\037\040\042\006\061\110\006\111\040\006\040\006\006\112\113\113\113\113\113").toCharArray ();
		private static char[] meta = ("\000\000\001\001\001\001\001\001\001\001\000\001\001\000\000\000").toCharArray ();
		private static char[] gotoDefault = ("\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\162\u0093\u0093\u0093\u0093\u0080\u0093\143\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093\u0093").toCharArray ();
		private static char[] lhs = ("\000\020\021\021\021\023\023\025\025\025\025\026\026\024\030\030\022\027\032\032\033\033\033\033\034\034\035\031\031\036\036\037\037\040\040\040\040\040\040\040\040\042\042\042\043\043\043\041\041\045\044").toCharArray ();
	}

	private final static class YYParserState	// internal tracking tool
	{
		int token;			// the current token type
		Object value;		// the current value associated with token
		int state;			// the current scan state

		YYParserState ()	// EOF token construction
		{
			this (0, null, 0);
		}
		YYParserState (int token)
		{
			this (token, null, 0);
		}
		YYParserState (int token, Object value)
		{
			this (token, value, 0);
		}
		YYParserState (int token, Object value, int state)
		{
			this.token = token;
			this.value = value;
			this.state = state;
		}
	}

	// lookahead stack for the parser
	private final LinkedList _yyLookaheadStack = new LinkedList ();
	// state stack for the parser
	private final Vector _yyStateStack = new Vector (512, 512);
	// flag that indicates error
	private boolean _yyInError;
	// internal track of the argument start
	private int _yyArgStart;
	// for passing value from lexer to parser
	private Object _yyValue;

	private InputStream _yyIs = System.in;
	private byte[] _yyBuffer;
	private int _yyBufferSize = 4096;
	private int _yyMatchStart;
	private int _yyBufferEnd;

	private int _yyBaseState;

	private int _yyTextStart;
	private int _yyLength;

	private Stack _yyLexerStack;
	private Stack _yyInputStack;


	/**
	 * Set the current input.
	 *
	 * @param	is
	 *			the new input.
	 */
	public void setInput (InputStream is)
	{
		_yyIs = is;
	}

	/**
	 * Obtain the current input.
	 *
	 * @return	the current input
	 */
	public InputStream getInput ()
	{
		return _yyIs;
	}

	/**
	 * Switch the current input to the new input.  The old input and already
	 * buffered characters are pushed onto the stack.
	 *
	 * @param	is
	 * 			the new input
	 */
	public void yyPushInput (InputStream is)
	{
		int len = _yyBufferEnd - _yyMatchStart;
		byte[] leftOver = new byte[len];
		System.arraycopy (_yyBuffer, _yyMatchStart, leftOver, 0, len);

		Object[] states = new Object[4];
		states[0] = _yyIs;
		states[1] = leftOver;

		if (_yyInputStack == null)
			_yyInputStack = new Stack ();
		_yyInputStack.push (states);

		_yyIs = is;
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
	}

	/**
	 * Switch the current input to the old input on stack.  The currently
	 * buffered characters are inserted infront of the old buffered characters.
	 */
	public void yyPopInput ()
	{
		Object[] states = (Object[])_yyInputStack.pop ();
		_yyIs = (InputStream)states[0];
		byte[] leftOver = (byte[])states[1];

		int curLen = _yyBufferEnd - _yyMatchStart;

		if ((leftOver.length + curLen) > _yyBuffer.length)
		{
			byte[] newBuffer = new byte[leftOver.length + curLen];
			System.arraycopy (_yyBuffer, _yyMatchStart, newBuffer, 0, curLen);
			System.arraycopy (leftOver, 0, newBuffer, curLen, leftOver.length);
			_yyBuffer = newBuffer;
			_yyMatchStart = 0;
			_yyBufferEnd = leftOver.length + curLen;
		}
		else
		{
			int start = _yyMatchStart;
			int end = _yyBufferEnd;
			byte[] buffer = _yyBuffer;

			for (int i = 0; start < end; ++i, ++start)
				buffer[i] = buffer[start];
			System.arraycopy (leftOver, 0, buffer, curLen, leftOver.length);
			_yyMatchStart = 0;
			_yyBufferEnd = leftOver.length + curLen;
		}
	}

	/**
	 * Obtain the number of input objects on the stack.
	 *
	 * @return	the number of input objects on the stack.
	 */
	public int yyInputStackSize ()
	{
		return _yyInputStack == null ? 0 : _yyInputStack.size ();
	}


	/**
	 * Get the current token text.
	 * <p>
	 * Avoid calling this function unless it is absolutely necessary since it creates
	 * a copy of the token string.  The string length can be found by reading _yyLength
	 * or calling yyLength () function.
	 *
	 * @return	the current text token.
	 */
	public String yyText ()
	{
		if (_yyMatchStart == _yyTextStart)		// this is the case when we have EOF
			return null;
		return new String (_yyBuffer, _yyTextStart, _yyMatchStart - _yyTextStart);
	}

	/**
	 * Get the current text token's length.  Actions specified in the CookCC file
	 * can directly access the variable _yyLength.
	 *
	 * @return	the string token length
	 */
	public int yyLength ()
	{
		return _yyLength;
	}

	/**
	 * Print the current string token to the standard output.
	 */
	public void echo ()
	{
		System.out.print (yyText ());
	}

	/**
	 * Put all but n characters back to the input stream.  Be aware that calling
	 * yyLess (0) is allowed, but be sure to change the state some how to avoid
	 * an endless loop.
	 *
	 * @param	n
	 * 			The number of characters.
	 */
	protected void yyLess (int n)
	{
		if (n < 0)
			throw new IllegalArgumentException ("yyLess function requires a non-zero value.");
		if (n > (_yyMatchStart - _yyTextStart))
			throw new IndexOutOfBoundsException ("yyLess function called with a too large index value " + n + ".");
		_yyMatchStart = _yyTextStart + n;
	}

	/**
	 * Set the lexer's current state.
	 *
	 * @param	baseState
	 *			the base state index
	 */
	protected void begin (int baseState)
	{
		_yyBaseState = baseState;
	}

	/**
	 * Push the current state onto lexer state onto stack and
	 * begin the new state specified by the user.
	 *
	 * @param	newState
	 *			the new state.
	 */
	protected void yyPushLexerState (int newState)
	{
		if (_yyLexerStack == null)
			_yyLexerStack = new Stack ();
		_yyLexerStack.push (new Integer (_yyBaseState));
		begin (newState);
	}

	/**
	 * Restore the previous lexer state.
	 */
	protected void yyPopLexerState ()
	{
		begin (((Integer)_yyLexerStack.pop ()).intValue ());
	}


	// read more data from the input
	protected boolean yyRefreshBuffer () throws IOException
	{
		if (_yyBuffer == null)
			_yyBuffer = new byte[_yyBufferSize];
		if (_yyMatchStart > 0)
		{
			if (_yyBufferEnd > _yyMatchStart)
			{
				System.arraycopy (_yyBuffer, _yyMatchStart, _yyBuffer, 0, _yyBufferEnd - _yyMatchStart);
				_yyBufferEnd -= _yyMatchStart;
				_yyMatchStart = 0;
			}
			else
			{
				_yyMatchStart = 0;
				_yyBufferEnd = 0;
			}
		}
		else if (_yyBufferEnd == _yyBuffer.length)
		{
			byte[] newBuffer = new byte[_yyBuffer.length + _yyBuffer.length / 2];
			System.arraycopy (_yyBuffer, 0, newBuffer, 0, _yyBufferEnd);
			_yyBuffer = newBuffer;
		}

		int readSize = _yyIs.read (_yyBuffer, _yyBufferEnd, _yyBuffer.length - _yyBufferEnd);
		if (readSize > 0)
			_yyBufferEnd += readSize;
		else if (readSize < 0 && !yyWrap ())		// since we are at EOF, call yyWrap ().  If the return value of yyWrap is false, refresh buffer again
			return yyRefreshBuffer ();
		return readSize >= 0;
	}

	/**
	 * Reset the internal buffer.
	 */
	public void yyResetBuffer ()
	{
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
	}

	/**
	 * Set the internal buffer size.  This action can only be performed
	 * when the buffer is empty.  Having a large buffer is useful to read
	 * a whole file in to increase the performance sometimes.
	 *
	 * @param	bufferSize
	 *			the new buffer size.
	 */
	public void setBufferSize (int bufferSize)
	{
		if (_yyBufferEnd > _yyMatchStart)
			throw new IllegalArgumentException ("Cannot change lexer buffer size at this moment.");
		_yyBufferSize = bufferSize;
		_yyMatchStart = 0;
		_yyBufferEnd = 0;
		if (_yyBuffer != null && bufferSize != _yyBuffer.length)
			_yyBuffer = new byte[bufferSize];
	}

	/**
	 * Call this function to start the scanning of the input.
	 *
	 * @return	a token or status value.
	 * @throws	IOException
	 *			in case of I/O error.
	 */
	protected int yyLex () throws IOException
	{

		char[] cc_ecs = cc_lexer.ecs;
		char[] cc_next = cc_lexer.next;
		char[] cc_check = cc_lexer.check;
		char[] cc_base = cc_lexer.base;
		char[] cc_default = cc_lexer.defaults;
		char[] cc_meta = cc_lexer.meta;
		char[] cc_accept = cc_lexer.accept;

		byte[] buffer = _yyBuffer;

		while (true)
		{
			// initiate variables necessary for lookup
			int cc_matchedState = _yyBaseState;

			int matchedLength = 0;

			int internalBufferEnd = _yyBufferEnd;
			int lookahead = _yyMatchStart;

			int cc_backupMatchedState = cc_matchedState;
			int cc_backupMatchedLength = 0;

			// the DFA lookup
			while (true)
			{
				// check buffer status
				if (lookahead < internalBufferEnd)
				{
					// now okay to process the character
					int cc_toState;
					int symbol = cc_ecs[buffer[lookahead] & 0xff];
					cc_toState = cc_matchedState;
					while (cc_check[symbol + cc_base[cc_toState]] != cc_toState)
					{
						cc_toState = cc_default[cc_toState];
						if (cc_toState >= 28)
							symbol = cc_meta[symbol];
					}
					cc_toState = cc_next[symbol + cc_base[cc_toState]];

					if (cc_toState == 0)
					{
						cc_matchedState = cc_backupMatchedState;
						matchedLength = cc_backupMatchedLength;
						break;
					}

					cc_matchedState = cc_toState;
					++lookahead;
					++matchedLength;

					if (cc_accept[cc_matchedState] > 0)
					{
						cc_backupMatchedState = cc_toState;
						cc_backupMatchedLength = matchedLength;
					}
				}
				else
				{
					int lookPos = lookahead - _yyMatchStart;
					boolean refresh = yyRefreshBuffer ();
					buffer = _yyBuffer;
					internalBufferEnd = _yyBufferEnd;
					lookahead = _yyMatchStart + lookPos;
					if (! refresh)
					{
						// <<EOF>>
						int cc_toState;
						int symbol = cc_ecs[256];
						cc_toState = cc_matchedState;
						while (cc_check[symbol + cc_base[cc_toState]] != cc_toState)
						{
							cc_toState = cc_default[cc_toState];
							if (cc_toState >= 28)
								symbol = cc_meta[symbol];
						}
						cc_toState = cc_next[symbol + cc_base[cc_toState]];

						if (cc_toState != 0)
							cc_matchedState = cc_toState;
						else
						{
							cc_matchedState = cc_backupMatchedState;
							matchedLength = cc_backupMatchedLength;
						}
						break;
					}
				}
			}

			_yyTextStart = _yyMatchStart;
			_yyMatchStart += matchedLength;
			_yyLength = matchedLength;


			switch (cc_accept[cc_matchedState])
			{
				case 1:	// [a-zA-Z_][_0-9a-zA-Z]*
				{
					_yyValue = m_this.lexA (); return ATOM;
				}
				case 23: break;
				case 2:	// @[a-zA-Z][0-9a-zA-Z]*
				{
					_yyValue = m_this.lexB (); return IVAR;
				}
				case 24: break;
				case 3:	// @@[a-zA-Z][0-9a-zA-Z]*
				{
					_yyValue = m_this.lexC (); return CVAR;
				}
				case 25: break;
				case 4:	// -?[0-9][0-9_]*
				{
					_yyValue = m_this.lexI (); return INTEGER;
				}
				case 26: break;
				case 5:	// #.*
				{
					m_this.lexD ();
				}
				case 27: break;
				case 6:	// \"
				{
					m_this.lexSS ();
				}
				case 28: break;
				case 7:	// [ 	]+
				{
					m_this.lexW ();
				}
				case 29: break;
				case 21:	// [^"]+\"
				{
					_yyValue = m_this.lexSC (); return STRING;
				}
				case 43: break;
				case 8:	// [{]
				{
					_yyValue = m_this.lexE (); return OBRACKET;
				}
				case 30: break;
				case 9:	// [}]
				{
					_yyValue = m_this.lexE (); return CBRACKET;
				}
				case 31: break;
				case 10:	// \+
				{
					_yyValue = m_this.lexE (); return PLUS;
				}
				case 32: break;
				case 11:	// -
				{
					_yyValue = m_this.lexE (); return MINUS;
				}
				case 33: break;
				case 12:	// ;
				{
					_yyValue = m_this.lexE (); return SEMICOLON;
				}
				case 34: break;
				case 13:	// [(]
				{
					_yyValue = m_this.lexE (); return OPAREN;
				}
				case 35: break;
				case 14:	// [)]
				{
					_yyValue = m_this.lexE (); return CPAREN;
				}
				case 36: break;
				case 15:	// [,]
				{
					_yyValue = m_this.lexE (); return COMMA;
				}
				case 37: break;
				case 16:	// =
				{
					_yyValue = m_this.lexE (); return ASSIGN;
				}
				case 38: break;
				case 17:	// \n
				{
					_yyValue = m_this.lexNewline (); return NEWLINE;
				}
				case 39: break;
				case 18:	// .
				{
					_yyValue = m_this.lexOther (); return UNKNOWN;
				}
				case 40: break;
				case 19:	// .|\n
				{
					echo ();			// default character action
				}
				case 41: break;
				case 20:	// <<EOF>>
				{
					return 0;			// default EOF action
				}
				case 42: break;
				default:
					throw new IOException ("Internal error in Parser lexer.");
			}

		}
	}


	/**
	 * Call this function to start parsing.
	 *
	 * @return	0 if everything is okay, or 1 if an error occurred.
	 * @throws	IOException
	 *			in case of error
	 */
	public int yyParse () throws IOException
	{
		char[] cc_ecs = cc_parser.ecs;
		char[] cc_next = cc_parser.next;
		char[] cc_check = cc_parser.check;
		char[] cc_base = cc_parser.base;
		char[] cc_default = cc_parser.defaults;
		char[] cc_meta = cc_parser.meta;
		char[] cc_gotoDefault = cc_parser.gotoDefault;
		char[] cc_rule = cc_parser.rule;
		char[] cc_lhs = cc_parser.lhs;

		LinkedList cc_lookaheadStack = _yyLookaheadStack;
		Vector cc_stateStack = _yyStateStack;

		if (cc_stateStack.size () == 0)
			cc_stateStack.add (new YYParserState ());

		int cc_toState = 0;

		for (;;)
		{
			YYParserState cc_lookahead;

			int cc_fromState;
			char cc_ch;

			//
			// check if there are any lookahead tokens on stack
			// if not, then call yyLex ()
			//
			if (cc_lookaheadStack.size () == 0)
			{
				_yyValue = null;
				int val = yyLex ();
				cc_lookahead = new YYParserState (val, _yyValue);
				cc_lookaheadStack.add (cc_lookahead);
			}
			else
				cc_lookahead = (YYParserState)cc_lookaheadStack.getLast ();

			cc_ch = cc_ecs[cc_lookahead.token];
			cc_fromState = ((YYParserState)cc_stateStack.get (cc_stateStack.size () - 1)).state;
			int cc_symbol = cc_ch;
			cc_toState = cc_fromState;
			while (cc_check[cc_symbol + cc_base[cc_toState]] != cc_toState)
			{
				cc_toState = cc_default[cc_toState];
				if (cc_toState >= 71)
					cc_symbol = cc_meta[cc_symbol];
			}
			cc_toState = (short)cc_next[cc_symbol + cc_base[cc_toState]];


			//
			// check the value of toState and determine what to do
			// with it
			//
			if (cc_toState > 0)
			{
				// shift
				cc_lookahead.state = cc_toState;
				cc_stateStack.add (cc_lookahead);
				cc_lookaheadStack.removeLast ();
				continue;
			}
			else if (cc_toState == 0)
			{
				// error
				if (_yyInError)
				{
					// first check if the error is at the lookahead
					if (cc_ch == 1)
					{
						// so we need to reduce the stack until a state with reduceable
						// action is found
						if (_yyStateStack.size () > 1)
							_yyStateStack.setSize (_yyStateStack.size () - 1);
						else
							return 1;	// can't do much we exit the parser
					}
					else
					{
						// this means that we need to dump the lookahead.
						if (cc_ch == 0)		// can't do much with EOF;
							return 1;
						cc_lookaheadStack.removeLast ();
					}
					continue;
				}
				else
				{
					if (yyParseError (cc_lookahead.token))
						return 1;
					_yyLookaheadStack.add (new YYParserState (1, _yyValue));
					_yyInError = true;
					continue;
				}
			}
			_yyInError = false;
			// now the reduce action
			int cc_ruleState = -cc_toState;

			_yyArgStart = cc_stateStack.size () - cc_rule[cc_ruleState] - 1;
			//
			// find the state that said need this non-terminal
			//
			cc_fromState = ((YYParserState)cc_stateStack.get (_yyArgStart)).state;

			//
			// find the state to goto after shifting the non-terminal
			// onto the stack.
			//
			if (cc_ruleState == 1)
				cc_toState = 0;			// reset the parser
			else
			{
				cc_toState = cc_fromState + 76;
				int cc_tmpCh = cc_lhs[cc_ruleState] - 16;
				while (cc_check[cc_tmpCh + cc_base[cc_toState]] != cc_toState)
					cc_toState = cc_gotoDefault[cc_toState - 76];
				cc_toState = cc_next[cc_tmpCh + cc_base[cc_toState]];
			}

			_yyValue = null;

			switch (cc_ruleState)
			{
				case 1:					// accept
					return 0;
				case 2:	// program : program require_stmt nl
				{
					m_this.parseA ();
				}
				case 53: break;
				case 3:	// program : program class_definition nl
				{
					m_this.parseA ();
				}
				case 54: break;
				case 4:	// program : nl
				{
					m_this.parseA ();
				}
				case 55: break;
				case 5:	// nl : nl NEWLINE
				{
					m_this.parseA ();
				}
				case 56: break;
				case 6:	// nl : 
				{
					m_this.parseA ();
				}
				case 57: break;
				case 7:	// sep : sep NEWLINE
				{
					m_this.parseA ();
				}
				case 58: break;
				case 8:	// sep : sep SEMICOLON
				{
					m_this.parseA ();
				}
				case 59: break;
				case 9:	// sep : NEWLINE
				{
					m_this.parseA ();
				}
				case 60: break;
				case 10:	// sep : SEMICOLON
				{
					m_this.parseA ();
				}
				case 61: break;
				case 11:	// optsep : sep
				{
					m_this.parseA ();
				}
				case 62: break;
				case 12:	// optsep : 
				{
					m_this.parseA ();
				}
				case 63: break;
				case 13:	// class_definition : class_name OBRACKET class_block CBRACKET
				{
					m_this.parseA ();
				}
				case 64: break;
				case 14:	// class_block : class_block method_declaration nl
				{
					m_this.parseA ();
				}
				case 65: break;
				case 15:	// class_block : nl
				{
					m_this.parseA ();
				}
				case 66: break;
				case 16:	// require_stmt : ATOM STRING
				{
					m_this.parseRequire ((java.lang.String)yyGetValue (1), (java.lang.String)yyGetValue (2));
				}
				case 67: break;
				case 17:	// class_name : ATOM ATOM
				{
					m_this.parseB ((java.lang.String)yyGetValue (1), (java.lang.String)yyGetValue (2));
				}
				case 68: break;
				case 18:	// pm : PLUS
				{
					_yyValue = m_this.parseC ();
				}
				case 69: break;
				case 19:	// pm : MINUS
				{
					_yyValue = m_this.parseD ();
				}
				case 70: break;
				case 20:	// args_defn : args_decl
				{
					_yyValue = m_this.parseArgumentsDefinition ((java.util.List<java.lang.String>)yyGetValue (1));
				}
				case 71: break;
				case 21:	// args_defn : OPAREN args_decl CPAREN
				{
					_yyValue = m_this.parseArgumentsDefinition ((java.util.List<java.lang.String>)yyGetValue (2));
				}
				case 72: break;
				case 22:	// args_defn : 
				{
					_yyValue = m_this.parseEmptyArgumentsDefinition ();
				}
				case 73: break;
				case 23:	// args_defn : OPAREN CPAREN
				{
					_yyValue = m_this.parseEmptyArgumentsDefinition ();
				}
				case 74: break;
				case 24:	// args_decl : ATOM
				{
					_yyValue = m_this.parseSingleArgumentDefinition ((java.lang.String)yyGetValue (1));
				}
				case 75: break;
				case 25:	// args_decl : args_decl COMMA ATOM
				{
					_yyValue = m_this.parseArgumentDefinition ((java.util.List<java.lang.String>)yyGetValue (1), (java.lang.String)yyGetValue (3));
				}
				case 76: break;
				case 26:	// method_header : pm ATOM args_defn
				{
					m_this.parseMethodHeader ((java.lang.Boolean)yyGetValue (1), (java.lang.String)yyGetValue (2), (java.util.List<java.lang.String>)yyGetValue (3));
				}
				case 77: break;
				case 27:	// method_declaration : method_header OBRACKET stmts1 CBRACKET
				{
					m_this.parseMDwithBody ((java.lang.Integer)yyGetValue (3));
				}
				case 78: break;
				case 28:	// method_declaration : method_header
				{
					m_this.parseMD ();
				}
				case 79: break;
				case 29:	// stmts1 : optsep stmts2 optsep
				{
					_yyValue = m_this.parseST1a ((java.lang.Integer)yyGetValue (2));
				}
				case 80: break;
				case 30:	// stmts1 : optsep
				{
					_yyValue = m_this.parseST1b ();
				}
				case 81: break;
				case 31:	// stmts2 : expr
				{
					_yyValue = m_this.parseST2a ((java.lang.Integer)yyGetValue (1));
				}
				case 82: break;
				case 32:	// stmts2 : stmts2 sep expr
				{
					_yyValue = m_this.parseST2b ((java.lang.Integer)yyGetValue (1), (java.lang.Integer)yyGetValue (3));
				}
				case 83: break;
				case 33:	// expr : STRING
				{
					_yyValue = m_this.parseString ((java.lang.String)yyGetValue (1));
				}
				case 84: break;
				case 34:	// expr : INTEGER
				{
					_yyValue = m_this.parseInteger ((java.lang.Integer)yyGetValue (1));
				}
				case 85: break;
				case 35:	// expr : ATOM args
				{
					_yyValue = m_this.parseMethodCall ((java.lang.String)yyGetValue (1), (java.util.List<java.lang.Integer>)yyGetValue (2));
				}
				case 86: break;
				case 36:	// expr : ATOM OPAREN args CPAREN
				{
					_yyValue = m_this.parseMethodCall ((java.lang.String)yyGetValue (1), (java.util.List<java.lang.Integer>)yyGetValue (3));
				}
				case 87: break;
				case 37:	// expr : ATOM ASSIGN expr
				{
					_yyValue = m_this.parseAssignment ((java.lang.String)yyGetValue (1), (java.lang.Integer)yyGetValue (3));
				}
				case 88: break;
				case 38:	// expr : ATOM
				{
					_yyValue = m_this.parseLocalVariable ((java.lang.String)yyGetValue (1));
				}
				case 89: break;
				case 39:	// expr : IVAR ASSIGN expr
				{
					_yyValue = m_this.parseStateAssignment ((java.lang.String)yyGetValue (1), (java.lang.Integer)yyGetValue (3));
				}
				case 90: break;
				case 40:	// expr : IVAR
				{
					_yyValue = m_this.parseInstanceVariable ((java.lang.String)yyGetValue (1));
				}
				case 91: break;
				case 41:	// aexpr : expr
				{
					_yyValue = m_this.parseSingleArgument ((java.lang.Integer)yyGetValue (1));
				}
				case 92: break;
				case 42:	// aexpr : aexpr COMMA expr
				{
					_yyValue = m_this.parseA ((java.util.List<java.lang.Integer>)yyGetValue (1), (java.lang.Integer)yyGetValue (3));
				}
				case 93: break;
				case 43:	// aexpr : ablock expr
				{
					_yyValue = m_this.parseA ((java.util.List<java.lang.Integer>)yyGetValue (1), (java.lang.Integer)yyGetValue (2));
				}
				case 94: break;
				case 44:	// ablock : block
				{
					_yyValue = m_this.parseSingleArgument ((java.lang.Integer)yyGetValue (1));
				}
				case 95: break;
				case 45:	// ablock : aexpr block
				{
					_yyValue = m_this.parseA ((java.util.List<java.lang.Integer>)yyGetValue (1), (java.lang.Integer)yyGetValue (2));
				}
				case 96: break;
				case 46:	// ablock : ablock block
				{
					_yyValue = m_this.parseA ((java.util.List<java.lang.Integer>)yyGetValue (1), (java.lang.Integer)yyGetValue (2));
				}
				case 97: break;
				case 47:	// args : aexpr
				{
					_yyValue = m_this.parseAN ((java.util.List<java.lang.Integer>)yyGetValue (1));
				}
				case 98: break;
				case 48:	// args : ablock
				{
					_yyValue = m_this.parseAN ((java.util.List<java.lang.Integer>)yyGetValue (1));
				}
				case 99: break;
				case 49:	// block0 : OBRACKET
				{
					_yyValue = m_this.parseBKS ();
				}
				case 100: break;
				case 50:	// block : block0 stmts1 CBRACKET
				{
					_yyValue = m_this.parseBK ((java.lang.Integer)yyGetValue (1), (java.lang.Integer)yyGetValue (2));
				}
				case 101: break;
				default:
					throw new IOException ("Internal error in Parser parser.");
			}

			YYParserState cc_reduced = new YYParserState (-cc_ruleState, _yyValue, cc_toState);
			_yyValue = null;
			cc_stateStack.setSize (_yyArgStart + 1);
			cc_stateStack.add (cc_reduced);
		}
	}

	/**
	 * This function is used by the error handling grammars to check the immediate
	 * lookahead token on the stack.
	 *
	 * @return	the top of lookahead stack.
	 */
	protected YYParserState yyPeekLookahead ()
	{
		return (YYParserState)_yyLookaheadStack.getLast ();
	}

	/**
	 * This function is used by the error handling grammars to pop an unwantted
	 * token from the lookahead stack.
	 */
	protected void yyPopLookahead ()
	{
		_yyLookaheadStack.removeLast ();
	}

	/**
	 * Clear the error flag.  If this flag is present and the parser again sees
	 * another error transition, it would immediately calls yyParseError, which
	 * would by default exit the parser.
	 * <p>
	 * This function is used in error recovery.
	 */
	protected void yyClearError ()
	{
		_yyInError = false;
	}

	/**
	 * This function reports error and return true if critical error occurred, or
	 * false if the error has been successfully recovered.  IOException is an optional
	 * choice of reporting error.
	 *
	 * @param	terminal
	 *			the terminal that caused the error.
	 * @return	true if irrecoverable error occurred.  Or simply throw an IOException.
	 *			false if the parsing can be continued to check for specific
	 *			error tokens.
	 * @throws	IOException
	 *			in case of error.
	 */
	protected boolean yyParseError (int terminal) throws IOException
	{
		return false;
	}

	/**
	 * Gets the object value associated with the symbol at the argument's position.
	 *
	 * @param	arg
	 *			the symbol position starting from 1.
	 * @return	the object value associated with symbol.
	 */
	protected Object yyGetValue (int arg)
	{
		return ((YYParserState)_yyStateStack.get (_yyArgStart + arg)).value;
	}

	/**
	 * Set the object value for the current non-terminal being reduced.
	 *
	 * @param	value
	 * 			the object value for the current non-terminal.
	 */
	protected void yySetValue (Object value)
	{
		_yyValue = value;
	}




	private final chaldea.parser.ChaldeaParser m_this = (chaldea.parser.ChaldeaParser)this;

	/**
	 * This function is used to change the initial state for the lexer.
	 *
	 * @param	state
	 *			the name of the state
	 */
	protected void begin (String state)
	{
		if ("INITIAL".equals (state))
		{
			begin (INITIAL);
			return;
		}
		if ("STRING_STATE".equals (state))
		{
			begin (STRING_STATE);
			return;
		}
		throw new IllegalArgumentException ("Unknown lexer state: " + state);
	}

	/**
	 * Push the current state onto lexer state onto stack and
	 * begin the new state specified by the user.
	 *
	 * @param	state
	 *			the new state.
	 */
	protected void yyPushLexerState (String state)
	{
		if ("INITIAL".equals (state))
		{
			yyPushLexerState (INITIAL);
			return;
		}
		if ("STRING_STATE".equals (state))
		{
			yyPushLexerState (STRING_STATE);
			return;
		}
		throw new IllegalArgumentException ("Unknown lexer state: " + state);
	}

	/**
	 * Check if there are more inputs.  This function is called when EOF is
	 * encountered.
	 *
	 * @return	true to indicate no more inputs.
	 * @throws	IOException
	 * 			in case of an IO error
	 */
	protected boolean yyWrap () throws IOException
	{
		return true;
	}


/*
 * lexer properties:
 * unicode = false
 * bol = false
 * backup = true
 * cases = 21
 * table = compressed
 * ecs = 19
 * states = 28
 * max symbol value = 256
 *
 * memory usage:
 * full table = 7196
 * ecs table = 789
 * next = 45
 * check = 45
 * default = 32
 * meta = 19
 * compressed table = 398
 *
 * parser properties:
 * symbols = 38
 * max terminal = 271
 * used terminals = 16
 * non-terminals = 22
 * rules = 50
 * shift/reduce conflicts = 0
 * reduct/reduce conflicts = 0
 *
 * memory usage:
 * ecs table = 2970
 * compressed table = 1334
 */
}
