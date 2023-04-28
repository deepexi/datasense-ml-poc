// Generated from Test.g4 by ANTLR 4.7.1

package com.deepexi.ds.antlr4;

import org.antlr.v4.runtime.Lexer;
import org.antlr.v4.runtime.CharStream;
import org.antlr.v4.runtime.Token;
import org.antlr.v4.runtime.TokenStream;
import org.antlr.v4.runtime.*;
import org.antlr.v4.runtime.atn.*;
import org.antlr.v4.runtime.dfa.DFA;
import org.antlr.v4.runtime.misc.*;

@SuppressWarnings({"all", "warnings", "unchecked", "unused", "cast"})
public class TestLexer extends Lexer {
	static { RuntimeMetaData.checkVersion("4.7.1", RuntimeMetaData.VERSION); }

	protected static final DFA[] _decisionToDFA;
	protected static final PredictionContextCache _sharedContextCache =
		new PredictionContextCache();
	public static final int
		NUL=1, ADD=2, DIV=3, MIN=4, INT=5, Digit=6, WS=7, NL=8, SHEBANG=9;
	public static String[] channelNames = {
		"DEFAULT_TOKEN_CHANNEL", "HIDDEN"
	};

	public static String[] modeNames = {
		"DEFAULT_MODE"
	};

	public static final String[] ruleNames = {
		"NUL", "ADD", "DIV", "MIN", "INT", "Digit", "WS", "NL", "SHEBANG"
	};

	private static final String[] _LITERAL_NAMES = {
		null, "'*'", "'+'", "'/'", "'-'"
	};
	private static final String[] _SYMBOLIC_NAMES = {
		null, "NUL", "ADD", "DIV", "MIN", "INT", "Digit", "WS", "NL", "SHEBANG"
	};
	public static final Vocabulary VOCABULARY = new VocabularyImpl(_LITERAL_NAMES, _SYMBOLIC_NAMES);

	/**
	 * @deprecated Use {@link #VOCABULARY} instead.
	 */
	@Deprecated
	public static final String[] tokenNames;
	static {
		tokenNames = new String[_SYMBOLIC_NAMES.length];
		for (int i = 0; i < tokenNames.length; i++) {
			tokenNames[i] = VOCABULARY.getLiteralName(i);
			if (tokenNames[i] == null) {
				tokenNames[i] = VOCABULARY.getSymbolicName(i);
			}

			if (tokenNames[i] == null) {
				tokenNames[i] = "<INVALID>";
			}
		}
	}

	@Override
	@Deprecated
	public String[] getTokenNames() {
		return tokenNames;
	}

	@Override

	public Vocabulary getVocabulary() {
		return VOCABULARY;
	}


	public TestLexer(CharStream input) {
		super(input);
		_interp = new LexerATNSimulator(this,_ATN,_decisionToDFA,_sharedContextCache);
	}

	@Override
	public String getGrammarFileName() { return "Test.g4"; }

	@Override
	public String[] getRuleNames() { return ruleNames; }

	@Override
	public String getSerializedATN() { return _serializedATN; }

	@Override
	public String[] getChannelNames() { return channelNames; }

	@Override
	public String[] getModeNames() { return modeNames; }

	@Override
	public ATN getATN() { return _ATN; }

	public static final String _serializedATN =
		"\3\u608b\ua72a\u8133\ub9ed\u417c\u3be7\u7786\u5964\2\13<\b\1\4\2\t\2\4"+
		"\3\t\3\4\4\t\4\4\5\t\5\4\6\t\6\4\7\t\7\4\b\t\b\4\t\t\t\4\n\t\n\3\2\3\2"+
		"\3\3\3\3\3\4\3\4\3\5\3\5\3\6\6\6\37\n\6\r\6\16\6 \3\7\3\7\3\b\6\b&\n\b"+
		"\r\b\16\b\'\3\b\3\b\3\t\5\t-\n\t\3\t\3\t\3\t\3\t\3\n\3\n\3\n\7\n\66\n"+
		"\n\f\n\16\n9\13\n\3\n\3\n\2\2\13\3\3\5\4\7\5\t\6\13\7\r\b\17\t\21\n\23"+
		"\13\3\2\5\3\2\62;\5\2\13\f\16\17\"\"\4\2\f\f\17\17\2?\2\3\3\2\2\2\2\5"+
		"\3\2\2\2\2\7\3\2\2\2\2\t\3\2\2\2\2\13\3\2\2\2\2\r\3\2\2\2\2\17\3\2\2\2"+
		"\2\21\3\2\2\2\2\23\3\2\2\2\3\25\3\2\2\2\5\27\3\2\2\2\7\31\3\2\2\2\t\33"+
		"\3\2\2\2\13\36\3\2\2\2\r\"\3\2\2\2\17%\3\2\2\2\21,\3\2\2\2\23\62\3\2\2"+
		"\2\25\26\7,\2\2\26\4\3\2\2\2\27\30\7-\2\2\30\6\3\2\2\2\31\32\7\61\2\2"+
		"\32\b\3\2\2\2\33\34\7/\2\2\34\n\3\2\2\2\35\37\5\r\7\2\36\35\3\2\2\2\37"+
		" \3\2\2\2 \36\3\2\2\2 !\3\2\2\2!\f\3\2\2\2\"#\t\2\2\2#\16\3\2\2\2$&\t"+
		"\3\2\2%$\3\2\2\2&\'\3\2\2\2\'%\3\2\2\2\'(\3\2\2\2()\3\2\2\2)*\b\b\2\2"+
		"*\20\3\2\2\2+-\7\17\2\2,+\3\2\2\2,-\3\2\2\2-.\3\2\2\2./\7\f\2\2/\60\3"+
		"\2\2\2\60\61\b\t\2\2\61\22\3\2\2\2\62\63\7%\2\2\63\67\7#\2\2\64\66\n\4"+
		"\2\2\65\64\3\2\2\2\669\3\2\2\2\67\65\3\2\2\2\678\3\2\2\28:\3\2\2\29\67"+
		"\3\2\2\2:;\b\n\3\2;\24\3\2\2\2\7\2 \',\67\4\b\2\2\2\3\2";
	public static final ATN _ATN =
		new ATNDeserializer().deserialize(_serializedATN.toCharArray());
	static {
		_decisionToDFA = new DFA[_ATN.getNumberOfDecisions()];
		for (int i = 0; i < _ATN.getNumberOfDecisions(); i++) {
			_decisionToDFA[i] = new DFA(_ATN.getDecisionState(i), i);
		}
	}
}