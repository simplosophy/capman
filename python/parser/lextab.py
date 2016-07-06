# lextab.py. This file automatically created by PLY (version 3.8). Don't edit!
_tabversion   = '3.8'
_lextokens    = set(['LPAR', 'OPTION', 'EXTEND', 'FIXED32', 'RPAR', 'REPEATED', 'TRUE', 'DOT', 'STRING', 'INT32', 'SERVICE', 'SEMI', 'OPTIONAL', 'REQUIRED', 'TO', 'RPC', 'NUM', 'EXTENSIONS', 'FIXED64', 'IMPORT', 'UINT32', 'SINT32', 'BLOCK_COMMENT', 'ENUM', 'LINE_COMMENT', 'RBRACE', 'PACKAGE', 'RBRACK', 'BYTES', 'RETURNS', 'INT64', 'MAX', 'EQ', 'STRING_LITERAL', 'UINT64', 'LBRACE', 'FALSE', 'NAME', 'SINT64', 'STARTTOKEN', 'FLOAT', 'LBRACK', 'SFIXED64', 'SFIXED32', 'BOOL', 'DOUBLE', 'EXTENDS', 'MESSAGE'])
_lexreflags   = 0
_lexliterals  = '()+-*/=?:,.^|&~!=[]{};<>@%'
_lexstateinfo = {'INITIAL': 'inclusive'}
_lexstatere   = {'INITIAL': [('(?P<t_BLOCK_COMMENT>/\\*(.|\\n)*?\\*/)|(?P<t_NAME>[A-Za-z_$][A-Za-z0-9._$]*)|(?P<t_newline>\\n+)|(?P<t_newline2>(\\r\\n)+)|(?P<t_STRING_LITERAL>\\"([^\\\\\\n]|(\\\\.))*?\\")|(?P<t_NUM>[+-]?\\d+)|(?P<t_ignore_LINE_COMMENT>//.*)|(?P<t_RPAR>\\))|(?P<t_DOT>\\.)|(?P<t_LPAR>\\()|(?P<t_LBRACK>\\[)|(?P<t_STARTTOKEN>\\+)|(?P<t_RBRACK>\\])|(?P<t_RBRACE>})|(?P<t_EQ>=)|(?P<t_LBRACE>{)|(?P<t_SEMI>;)', [None, ('t_BLOCK_COMMENT', 'BLOCK_COMMENT'), None, ('t_NAME', 'NAME'), ('t_newline', 'newline'), ('t_newline2', 'newline2'), None, (None, 'STRING_LITERAL'), None, None, (None, 'NUM'), (None, None), (None, 'RPAR'), (None, 'DOT'), (None, 'LPAR'), (None, 'LBRACK'), (None, 'STARTTOKEN'), (None, 'RBRACK'), (None, 'RBRACE'), (None, 'EQ'), (None, 'LBRACE'), (None, 'SEMI')])]}
_lexstateignore = {'INITIAL': ' \t\x0c'}
_lexstateerrorf = {'INITIAL': 't_error'}
_lexstateeoff = {}
