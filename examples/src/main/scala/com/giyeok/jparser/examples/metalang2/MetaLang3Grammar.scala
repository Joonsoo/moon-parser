package com.giyeok.jparser.examples.metalang2

import com.giyeok.jparser.examples.{MetaLang2Example, MetaLang3Example, MetaLangExamples}

// meta lang 3는 2에 비해
// - A&B, A-B, ^A, !A에 대한 처리 방식이 정해짐.
//   - A&B와 A-B에서 실제 파싱 결과로써 의미 있는 것은 앞에 있는 A이고, ^A나 !A는 기본적으로 빈 sequence를 의미한다.
//     그래서 그냥 reference하면 A&B나 A-B에서는 A, ^A나 !A에서는 빈 sequence를 반환한다. 조건으로 붙는 부분(A&B나 A-B의
//     B와 ^A, !A의 A)이 필요하면 conditional symbol traverse path를 이용해서 값을 얻는다.
//   - conditional symbol traverse path는 '<'는 왼쪽(A&B, A-B에서 A, ^A, !A에서 빈 sequence), '>'는 오른쪽(A&B, A-B에서
//     B, ^A, !A에서 A)를 가리킨다. conditional symbol이 중첩될 수 있기 때문에 path로 나타냄.
//     - 예를 들어 A&(B&C)에서 '<'이면 A, '><'이면 B, '>>'이면 C.
//   - A&B를 $로 reference하면 A를 가리키는 것이 됨. B를 reference하려면 $>0라고(A&B가 0번째이면) 써야 함. ">"는 cond symbol traverse path.
//   - A-B도 A&B와 동일.
//   - ^A를 $로 reference하면 항상 빈 ParseNode sequence가 반환된다. $>0으로(^A가 0번째이면) 가리키면 A를 반환한다.
//   - !A도 ^A와 동일.
// - raw symbol ref 추가
//   - \$0 은 0번 symbol의 가공되지 않은 ParseNode 그 자체를 나타낸다. 여기서도 cond symbol traverse path 사용 가능
// - InPlaceSequence 안에서 Symbol 뿐만 아니라 Processor도 사용 가능해졌다. InPlaceSequence 내에서도 일반적인 RHS에서와
//   마찬가지로 가장 마지막 element가 전체 sequence를 대변하는 값이 된다.
// - bounded expression의 의미가 정립되었다.
//   - bounded expression은 InPlaceSequence의 내용을 처리해야하는 상황에 사용할 수 있는 syntactic sugar로 본다.
//   - 예를 들어, "A (WS ',' WS A)* {[$0] + $1$3}"은 사실 "A (WS ',' WS A $3)* {[$0] + $1}"을 다르게 쓴 것이다.
//   - bounded expression $x{E}은 $x가 단 하나의 InPlaceSequence를 갖는 InPlaceChoices이거나(이하 InPlaceSingleChoice),
//     InPlaceSingleChoice?, InPlaceSingleChoice*, InPlaceSingleChoice+, <InPlaceSingleChoice>일 때 사용할 수 있다.
//   - "A*?" 같이 repeat가 중첩된 케이스에는 bounded expression을 사용할 수 없다.
//   - "<A*>"에서는 "A*"가 InPlaceSingleChoice로 해석되므로 bounded expression을 사용할 수 있다.
// - nested nullable type은 여전히 지원하지 않음. 단 의미를 좀더 분명히 함.
//   - metalang2와 마찬가지로 nested nullable type은 지원하지 않는다. (코틀린도 T??는 T?와 같은 의미)
//   - "A???" symbol이 스칼라식으로 "Option[Option[Option[A]]]"인데 A의 값이 있으면 "Some(Some(Some(a)))"이 되고,
//     A가 비었으면 "None", "Some(None)", "Some(Some(None))" 셋 중 하나일 수 있지만, 실제로는 구분할 의미가 없는듯.
//   - 그래서 "A???"에서 A가 비었으면 None, 비지 않았으면 Some(Some(Some(a)))로 한다.
//   - 사실 "A???"는 A가 비어있는 경우 ambiguous하기 때문에 파싱 트리가 세개 나오고, 각각 None, Some(None),
//     Some(Some(None))에 매핑할 수 있다. 하지만 구분하는 의미가 없기 때문에 Some(None)과 Some(Some(None)) 모두 None으로
//   - bounded expr은 중첩된 repeat에서는 사용할 수 없다.
//   - 하지만 A???가 정말 필요할까..?
// - 각종 이름 escape 기능 지원 (string이란 이름은 예약어라 안되지만 `string`은 사용 가능)
// - 기본적인 programming 지원
//   - bool, char, string 과 같은 기본적인 타입 지원
//     - bool literal: true/false
//     - "null" literal for nullable types
//     - int나 float같은 숫자가 들어가면 문제가 너무 복잡해져서 char, string과 같이 파싱과 바로 연결되는 타입만 지원.
//   - 간단한 함수식 지원
//     - isempty(x: [T]): bool = if (x.isEmpty()) true else false
//     - isempty(x: T?): bool = if (x == null) true else false
//     - ispresent(x: [T] | T?): bool = not(isempty(x))
//     - chr(x: Terminal Node): chr
//     - str(x: Node): str
//     - int/int32(x: str|Node): int32
//     - int64(x: str|Node): int64
//     - cond? then:else
//       - ispresent($0)? %OpType.ADD:null
//     - bool && bool, bool || bool, !bool
//     - A ?: B (elvis operator in kotlin, nullish coalescing in Typescript)
//     - str == str, str != str, char == char, char != char. [str], [[str]], 도 비교할 수 있지 않을까?
//     - (a:[T]) + (b:[T])
//     - (a:str) + (b:str)
//     - 우선순위: (+) (?:) (==, !=) (&&) (||) (? :)
//   - enum type 지원
//     - EnumType = '%' Name '{' Name (',' Name)* '}'
//     - EnumValue = '%' Name '.' Name
// - type def/on-the-fly type def 문법 변경
//   - '@' 제거
//   - 이제 사용자 입장에서 개념적으로 on-the-fly type def라는 것이 별도로 필요치 않음
// - construct할 때 parameter에 이름 붙이는 것 가능
//   - parameter 순서를 바꾸거나 비워두는 named parameter 기능을 추가하는 것은 아니고, 그냥 인자 갯수/순서/이름이 모두
//     일치하면 에러를 발생시키지 않는 것.
// - 기본적으로 meta lang은 최대한 많은 내용을 유추해서 코드를 만들어주는 데 있음.
//   - 이를테면 타입 hierarchy, 각 parameter의 타입, enum에 포함되는 값 등등
object MetaLang3Grammar extends MetaLangExamples {
    // Ref가 metalang2와 달리 세가지로 세분화됨.
    // - $는 의미적 reference. $0가 A*를 가리키고 있으면 $0는 A의 리스트가 된다.
    // - $$는 conditional nonterminal의 condition 부분에 대한 reference. $$는 A&B, A-B, ^A, !A 심볼에 대해서만 사용 가능
    // - \$는 raw ParseNode에 대한 refernce. \$0가 A*를 가리키고 있으면 \$0는 A의 리스트가 아니라 A* 자체의 ParseNode

    val inMetaLang2: MetaLang2Example = MetaLang2Example("Meta Language 3",
        """Grammar = WS Def (WS Def)* WS {@Grammar(defs=[$1] + $2$1)}
          |Def: @Def = Rule | TypeDef
          |
          |Rule = LHS WS '=' WS RHS (WS '|' WS RHS)* {@Rule(lhs=$0, rhs=[$4] + $5$3)}
          |LHS = Nonterminal (WS ':' WS TypeDesc)? {@LHS(name=$0, typeDesc=$1$3)}
          |RHS = Elem (WS Elem)* {@RHS(elems=[$0] + $1$1)}
          |Elem: @Elem = Symbol | Processor
          |
          |
          |// Symbol
          |Symbol: @Symbol = BinSymbol
          |BinSymbol: @BinSymbol = BinSymbol WS '&' WS PreUnSymbol {@JoinSymbol(body=$0, join=$4)}
          |  | BinSymbol WS '-' WS PreUnSymbol {@ExceptSymbol(body=$0, except=$4)}
          |  | PreUnSymbol
          |PreUnSymbol: @PreUnSymbol = '^' WS PreUnSymbol {@FollowedBy(followedBy=$2)}
          |  | '!' WS PreUnSymbol {@NotFollowedBy(notFollowedBy=$2)}
          |  | PostUnSymbol
          |PostUnSymbol: @PostUnSymbol = PostUnSymbol WS '?' {@Optional(body=$0)}
          |  | PostUnSymbol WS '*' {@RepeatFromZero(body=$0)}
          |  | PostUnSymbol WS '+' {@RepeatFromOne(body=$0)}
          |  | AtomSymbol
          |AtomSymbol: @AtomSymbol = Terminal
          |  | TerminalChoice
          |  | StringSymbol
          |  | Nonterminal
          |  | '(' WS InPlaceChoices WS ')' $2
          |  | Longest
          |  | EmptySequence
          |Terminal: Terminal = '\'' TerminalChar '\'' $1
          |  | '.' {@AnyTerminal()}
          |TerminalChoice = '\'' TerminalChoiceElem TerminalChoiceElem+ '\'' {@TerminalChoice(choices=[$1] + $2)}
          |  | '\'' TerminalChoiceRange '\'' {TerminalChoice([$1])}
          |TerminalChoiceElem: @TerminalChoiceElem = TerminalChoiceChar
          |  | TerminalChoiceRange
          |TerminalChoiceRange = TerminalChoiceChar '-' TerminalChoiceChar {@TerminalChoiceRange(start=$0, end=$2)}
          |StringSymbol = '"' StringChar* '"' {@StringSymbol(value=$1$0)}
          |Nonterminal = Id {@Nonterminal(name=$0)}
          |InPlaceChoices = InPlaceSequence (WS '|' WS InPlaceSequence)* {@InPlaceChoices(choices=[$0] + $1$3)}
          |InPlaceSequence = Elem (WS Elem)* {@InPlaceSequence(seq=[$0] + $1$1)}
          |Longest = '<' WS InPlaceChoices WS '>' {@Longest(choices=$2)}
          |EmptySequence = '#' {@EmptySeq()}
          |
          |TerminalChar: TerminalChar = .-'\\' {@CharAsIs(value=$0)}
          |  | '\\' '\'\\bnrt' {@CharEscaped(escapeCode=$1)}
          |  | UnicodeChar
          |TerminalChoiceChar: TerminalChoiceChar = .-'\'\-\\' {CharAsIs($0)}
          |  | '\\' '\'\-\\bnrt' {CharEscaped($1)}
          |  | UnicodeChar
          |StringChar: StringChar = .-'"\\' {CharAsIs($0)}
          |  | '\\' '"\\bnrt' {CharEscaped($1)}
          |  | UnicodeChar
          |UnicodeChar = '\\' 'u' '0-9A-Fa-f' '0-9A-Fa-f' '0-9A-Fa-f' '0-9A-Fa-f' {@CharUnicode(code=[$2, $3, $4, $5])}
          |
          |
          |// Processor
          |Processor: Processor = Ref
          |  | '{' WS PExpr WS '}' $2
          |
          |Ref: @Ref = ValRef | RawRef
          |ValRef = '$' CondSymPath? RefIdx {@ValRef(idx=$2, condSymPath=$1)}
          |CondSymPath = ('<' | '>')+
          |RawRef = "\\$" CondSymPath? RefIdx {@RawRef(idx=$2, condSymPath=$1)}
          |
          |// 우선순위 낮은것부터
          |PExpr = TernateryExpr // TODO (WS ':' WS TypeDesc)? 를 뒤에 붙일 수 있을까?
          |TernateryExpr = BoolOrExpr WS '?' WS <TernateryExpr> WS ':' WS <TernateryExpr> {@TerOp(cond=$0, then=$4, otherwise=$8)}
          |  | BoolOrExpr
          |BoolOrExpr = BoolAndExpr WS "&&" WS BoolOrExpr {@BinOp(lhs=$0, rhs=$4, op=$2)}
          |  | BoolAndExpr
          |BoolAndExpr = BoolEqExpr WS "||" WS BoolAndExpr {BinOp($0, $4, $2)}
          |  | BoolEqExpr
          |BoolEqExpr = ElvisExpr WS ("==" | "!=") WS BoolEqExpr {BinOp($0, $4, $2)}
          |  | ElvisExpr
          |ElvisExpr = AdditiveExpr WS "?:" WS ElvisExpr {@Elvis(value=$0, whenNull=$4)}
          |  | AdditiveExpr
          |AdditiveExpr = PrefixNotExpr WS '+' WS AdditiveExpr {BinOp($0, $4, $2)}
          |  | PrefixNotExpr
          |PrefixNotExpr = '!' WS PrefixNotExpr {@PreOp(expr=$2, op=$0)}
          |  | Atom
          |Atom = Ref
          |  | BindExpr
          |  | NamedConstructExpr
          |  | FuncCallOrConstructExpr
          |  | ArrayExpr
          |  | Literal
          |  | EnumValue
          |  | '(' WS PExpr WS ')' {@ExprParen(body=$2)}
          |
          |BindExpr = ValRef BinderExpr {@BindExpr(ctx=$0, binder=$1)}
          |BinderExpr = Ref
          |  | BindExpr
          |  | '{' WS PExpr WS '}' $2
          |NamedConstructExpr = TypeName WS NamedConstructParams {@NamedConstructExpr(typeName=$0, params=$2)}
          |NamedConstructParams = '(' WS (NamedParam (WS ',' WS NamedParam)* WS)? ')' {$2{[$0] + $1$3}}
          |NamedParam = ParamName (WS ':' WS TypeDesc)? WS '=' WS PExpr {@NamedParam(name=$0, typeDesc=$1, expr=$5)}
          |FuncCallOrConstructExpr = TypeOrFuncName WS CallParams {@FuncCallOrConstructExpr(funcName=$0, params=$2)}
          |CallParams = '(' WS (PExpr (WS ',' WS PExpr)* WS)? ')' {$2{[$0] + $1$3}}
          |ArrayExpr = '[' WS (PExpr (WS ',' WS PExpr)* WS)? ']' {@ArrayExpr(elems=$2{[$0] + $1})}
          |Literal: Literal = "null" {NullLiteral()}
          |  | ("true" | "false") {@BoolLiteral(value=$0)}
          |  | '\'' CharChar '\'' {@CharLiteral(value=$1)}
          |  | '"' StrChar* '"' {@StringLiteral(value=$1)}
          |EnumValue: AbstractEnumValue = <CanonicalEnumValue | ShortenedEnumValue>
          |CanonicalEnumValue = EnumTypeName '.' Id {@CanonicalEnumValue(enumName=$0, valueName=$2)}
          |// ShortenedEnumValue는 어떤 Enum 값인지 외부의 정보로부터 확실히 알 수 있을 때만 사용 가능
          |ShortenedEnumValue = '%' Id {@ShortenedEnumValue(valueName=$1)}
          |
          |
          |// TypeDef
          |TypeDef: @TypeDef = ClassDef
          |  | SuperDef // SuperDef는 super class가 자신의 sub class를 리스팅하는 식으로 정의하는 것.
          |  | EnumTypeDef
          |ClassDef = TypeName WS SuperTypes {@AbstractClassDef(name=$0, supers=$2)}
          |  | TypeName WS ClassParamsDef {@ConcreteClassDef(name=$0, supers=[], params=$2)}
          |  | TypeName WS SuperTypes WS ClassParamsDef {ConcreteClassDef($0, $2, $4)}
          |SuperTypes = '<' WS (TypeName (WS ',' WS TypeName)* WS)? '>' {$2{[$0] + $1$3}}
          |ClassParamsDef = '(' WS (ClassParamDef (WS ',' WS ClassParamDef)* WS)? WS ')' {$2{[$0] + $1$3}}
          |ClassParamDef = ParamName (WS ':' WS TypeDesc)? {@ClassParamDef(name=$0, typeDesc=$1$3)}
          |
          |SuperDef = TypeName WS '{' WS (SubTypes WS)? '}' {@SuperDef(typeName=$0, subs=$4$0)}
          |SubTypes = SubType (WS ',' WS SubType)* {[$0] + $1$3}
          |SubType: SubType = TypeName | ClassDef | SuperDef
          |
          |EnumTypeDef = EnumTypeName WS '{' WS Id (WS ',' WS Id)* WS '}'
          |
          |
          |// TypeDesc
          |TypeDesc = NonNullTypeDesc (WS '?')? {@TypeDesc(typ=$0, optional=$1)}
          |NonNullTypeDesc: NonNullTypeDesc = TypeName
          |  | '[' WS TypeDesc WS ']' {@ArrayTypeDesc(elemType=$2)}
          |  | ValueType
          |  | EnumTypeName
          |  | TypeDef
          |
          |ValueType: @ValueType = "boolean" {@BooleanType()}
          |  | "char" {@CharType()}
          |  | "string" {@StringType()}
          |  | "null" {NullType()}
          |EnumTypeName = '%' Id
          |// EnumTypeDef로 enum의 모든 값이 한군데서 정의되었으면 이값들만 사용되어야 한다.
          |
          |
          |// Common
          |TypeName = Id {@TypeName(name=$0)}
          |TypeOrFuncName = Id {@TypeOrFuncName(name=$0)}
          |ParamName = Id {@ParamName(name=$0)}
          |StrChar = StringChar
          |CharChar = TerminalChar
          |
          |RefIdx = <'0' | '1-9' '0-9'*>
          |Id = <'a-zA-Z_' 'a-zA-Z0-9_'*>
          |WS = (' \n\r\t' | LineComment)*
          |LineComment = '/' '/' (.-'\n')* (EOF | '\n')
          |EOF = !.
          |""".stripMargin)

    val inMetaLang3: MetaLang3Example = MetaLang3Example("Meta Language 3",
        """Grammar = WS Def (WS Def)* WS {Grammar(defs=[$1] + $2)}
          |Def: Def = Rule | TypeDef
          |
          |Rule = LHS WS '=' WS (RHS (WS '|' WS RHS)* {[$0] + $1}) {Rule(lhs=$0, rhs=$4)}
          |LHS = Nonterminal (WS ':' WS TypeDesc)? {LHS(name=$0, typeDesc=$1)}
          |RHS = Elem (WS Elem)* {RHS(elems=[$0] + $1)}
          |Elem: Elem = Symbol | Processor
          |
          |
          |// Symbol
          |Symbol: Symbol = BinSymbol
          |BinSymbol: BinSymbol = BinSymbol WS '&' WS PreUnSymbol {JoinSymbol(body=$0, join=$4)}
          |  | BinSymbol WS '-' WS PreUnSymbol {ExceptSymbol(body=$0, except=$4)}
          |  | PreUnSymbol
          |PreUnSymbol: PreUnSymbol = '^' WS PreUnSymbol {FollowedBy(followedBy=$2)}
          |  | '!' WS PreUnSymbol {NotFollowedBy(notFollowedBy=$2)}
          |  | PostUnSymbol
          |PostUnSymbol: PostUnSymbol = PostUnSymbol WS '?' {Optional(body=$0)}
          |  | PostUnSymbol WS '*' {RepeatFromZero(body=$0)}
          |  | PostUnSymbol WS '+' {RepeatFromOne(body=$0)}
          |  | AtomSymbol
          |AtomSymbol: AtomSymbol = Terminal
          |  | TerminalChoice
          |  | StringSymbol
          |  | Nonterminal
          |  | '(' WS InPlaceChoices WS ')' {Paren(choices=$2)}
          |  | Longest
          |  | EmptySequence {EmptySeq()}
          |Terminal: Terminal = '\'' TerminalChar '\'' $1
          |  | '.' {AnyTerminal()}
          |TerminalChoice = '\'' TerminalChoiceElem TerminalChoiceElem+ '\'' {TerminalChoice(choices=[$1] + $2)}
          |  | '\'' TerminalChoiceRange '\'' {TerminalChoice(choices=[$1])}
          |TerminalChoiceElem: TerminalChoiceElem = TerminalChoiceChar
          |  | TerminalChoiceRange
          |TerminalChoiceRange = TerminalChoiceChar '-' TerminalChoiceChar {TerminalChoiceRange(start=$0, end=$2)}
          |StringSymbol = '"' StringChar* '"' {StringSymbol(value=$1$0)}
          |Nonterminal = Id {Nonterminal(name=$0)}
          |InPlaceChoices = InPlaceSequence (WS '|' WS InPlaceSequence)* {InPlaceChoices(choices=[$0] + $1)}
          |InPlaceSequence = Elem (WS Elem)* {InPlaceSequence(seq=[$0] + $1)}
          |Longest = '<' WS InPlaceChoices WS '>' {Longest(choices=$2)}
          |EmptySequence = '#'
          |
          |TerminalChar: TerminalChar = .-'\\' {CharAsIs(value=chr($0))}
          |  | '\\' '\'\\bnrt' {CharEscaped(escapeCode=chr($1))}
          |  | UnicodeChar
          |TerminalChoiceChar: TerminalChoiceChar = .-'\'\-\\' {CharAsIs(value=chr($0))}
          |  | '\\' '\'\-\\bnrt' {CharEscaped(escapeCode=chr($1))}
          |  | UnicodeChar
          |StringChar: StringChar = .-'"\\' {CharAsIs(value=chr($0))}
          |  | '\\' '"\\bnrt' {CharEscaped(escapeCode=chr($1))}
          |  | UnicodeChar
          |UnicodeChar = '\\' 'u' '0-9A-Fa-f' '0-9A-Fa-f' '0-9A-Fa-f' '0-9A-Fa-f' {CharUnicode(code=[chr($2), chr($3), chr($4), chr($5)])}
          |
          |
          |// Processor
          |Processor: Processor = Ref
          |  | '{' WS PExpr WS '}' $2
          |
          |Ref: Ref = ValRef | RawRef
          |ValRef = '$' CondSymPath? RefIdx {ValRef(idx=$2, condSymPath=$1)}
          |CondSymPath: [%CondSymDir{BODY, COND}] = ('<' {%BODY} | '>' {%COND})+
          |RawRef = "\\$" CondSymPath? RefIdx {RawRef(idx=$2, condSymPath=$1)}
          |
          |// 우선순위 낮은것부터
          |PExpr = TernateryExpr // TODO (WS ':' WS TypeDesc)? 를 뒤에 붙일 수 있을까?
          |TernateryExpr = BoolOrExpr WS '?' WS <TernateryExpr> WS ':' WS <TernateryExpr> {TerOp(cond=$0, then=$4, otherwise=$8)}
          |  | BoolOrExpr
          |BoolOrExpr = BoolAndExpr WS "&&" WS BoolOrExpr {BinOp(lhs=$0, rhs=$4, op=%Op.AND)}
          |  | BoolAndExpr
          |BoolAndExpr = BoolEqExpr WS "||" WS BoolAndExpr {BinOp(lhs=$0, rhs=$4, op=%Op.OR)}
          |  | BoolEqExpr
          |BoolEqExpr = ElvisExpr WS ("==" {%Op.EQ} | "!=" {%Op.NE}) WS BoolEqExpr {BinOp(lhs=$0, rhs=$4, op=$2)}
          |  | ElvisExpr
          |ElvisExpr = AdditiveExpr WS "?:" WS ElvisExpr {Elvis(value=$0, whenNull=$4)}
          |  | AdditiveExpr
          |AdditiveExpr = PrefixNotExpr WS ('+' {%Op.ADD}) WS AdditiveExpr {BinOp(lhs=$0, rhs=$4, op=$2)}
          |  | PrefixNotExpr
          |PrefixNotExpr = '!' WS PrefixNotExpr
          |  | Atom
          |Atom = Ref
          |  | BindExpr
          |  | NamedConstructExpr
          |  | FuncCallOrConstructExpr
          |  | ArrayExpr
          |  | Literal
          |  | EnumValue
          |  | '(' WS PExpr WS ')' {ExprParen(body=$2)}
          |
          |BindExpr = ValRef BinderExpr
          |BinderExpr = Ref
          |  | BindExpr
          |  | '{' WS PExpr WS '}' $2
          |NamedConstructExpr = TypeName WS NamedConstructParams {NamedConstructExpr(typeName=$0, params=$2)}
          |NamedConstructParams = '(' WS (NamedParam (WS ',' WS NamedParam {[$0] + $1})* WS)? ')' $2
          |NamedParam = ParamName (WS ':' WS TypeDesc)? WS '=' WS PExpr {NamedParam(name=$0, typeDesc=$1, expr=$5)}
          |FuncCallOrConstructExpr = TypeOrFuncName WS CallParams {FuncCallOrConstructExpr(funcName=$0, params=$2)}
          |CallParams = '(' WS (PExpr (WS ',' WS PExpr)* WS {[$0] + $1})? ')' {$2 ?: []}
          |ArrayExpr = '[' WS (PExpr (WS ',' WS PExpr)* WS)? ']' {ArrayExpr(elems=$2{[$0] + $1} ?: [])}
          |Literal: Literal = "null" {NullLiteral()}
          |  | ("true" {true} | "false" {false}) {BoolLiteral(value=$0)}
          |  | '\'' CharChar '\'' {CharLiteral(value=$1)}
          |  | '"' StrChar* '"' {StrLiteral(value=$1)}
          |EnumValue: AbstractEnumValue = <CanonicalEnumValue | ShortenedEnumValue>
          |CanonicalEnumValue = EnumTypeName '.' Id {CanonicalEnumValue(enumName=$0, valueName=$2)}
          |// ShortenedEnumValue는 어떤 Enum 값인지 외부의 정보로부터 확실히 알 수 있을 때만 사용 가능
          |ShortenedEnumValue = '%' Id {ShortenedEnumValue(valueName=$1)}
          |
          |
          |// TypeDef
          |TypeDef: TypeDef = ClassDef
          |  | SuperDef // SuperDef는 super class가 자신의 sub class를 리스팅하는 식으로 정의하는 것.
          |  | EnumTypeDef
          |ClassDef = TypeName WS SuperTypes {AbstractClassDef(name=$0, supers=$2)}
          |  | TypeName WS ClassParamsDef {ConcreteClassDef(name=$0, supers=[], params=$2)}
          |  | TypeName WS SuperTypes WS ClassParamsDef {ConcreteClassDef(name=$0, supers=$2, params=$4)}
          |SuperTypes = '<' WS (TypeName (WS ',' WS TypeName)* WS {[$0] + $1})? '>' {$2 ?: []}
          |ClassParamsDef = '(' WS (ClassParamDef (WS ',' WS ClassParamDef)* WS {[$0] + $1})? WS ')' {$2 ?: []}
          |ClassParamDef = ParamName (WS ':' WS TypeDesc)? {ClassParamDef(name=$0, typeDesc=$1)}
          |
          |SuperDef = TypeName WS '{' WS (SubTypes WS)? '}' {SuperDef(typeName=$0, subs=$4$0)}
          |SubTypes = SubType (WS ',' WS SubType)* {[$0] + $1}
          |SubType: SubType = TypeName | ClassDef | SuperDef
          |
          |EnumTypeDef = EnumTypeName WS '{' WS Id (WS ',' WS Id)* WS '}'
          |
          |
          |// TypeDesc
          |TypeDesc = NonNullTypeDesc (WS '?')? {TypeDesc(typ=$0, optional=ispresent($1))}
          |NonNullTypeDesc: NonNullTypeDesc = TypeName
          |  | '[' WS TypeDesc WS ']' {ArrayTypeDesc(elemType=$2)}
          |  | ValueType
          |  | EnumTypeName
          |  | TypeDef
          |
          |ValueType: ValueType = "boolean" {BooleanType()}
          |  | "char" {CharType()}
          |  | "string" {StringType()}
          |  | "null" {NullType()}
          |EnumTypeName = '%' Id
          |// EnumTypeDef로 enum의 모든 값이 한군데서 정의되었으면 이값들만 사용되어야 한다.
          |
          |
          |// Common
          |TypeName = Id {TypeName(name=$0)}
          |TypeOrFuncName = Id {TypeOrFuncName(name=$0)}
          |ParamName = Id {ParamName(name=$0)}
          |StrChar = StringChar
          |CharChar = TerminalChar
          |
          |RefIdx = <'0' | '1-9' '0-9'*>
          |Id = <'a-zA-Z_' 'a-zA-Z0-9_'*>
          |WS = (' \n\r\t' | LineComment)*
          |LineComment = '/' '/' (.-'\n')* (EOF | '\n')
          |EOF = !.
          |""".stripMargin)

    val examples = List(inMetaLang2, inMetaLang3)
}