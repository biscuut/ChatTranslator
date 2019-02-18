package codes.biscuit.chattranslator.utils;

public enum YandexLanguage {
    AZERBAIJAN("az"),
    ALBANIAN("sq"),
    AMHARIC("am"),
    ENGLISH("en"),
    ARABIC("ar"),
    ARMENIAN("hy"),
    AFRIKAANS("af"),
    BASQUE("eu"),
    BASHKIR("ba"),
    BELARUSIAN("be"),
    BENGALI("bn"),
    BURMESE("my"),
    BULGARIAN("bg"),
    BOSNIAN("bs"),
    WELSH("cy"),
    HUNGARIAN("hu"),
    VIETNAMESE("vi"),
    HAITIAN("ht"),
    GALICIAN("gl"),
    DUTCH("nl"),
    HILLMARI("mrj"),
    GREEK("el"),
    GEORGIAN("ka"),
    GUJARATI("gu"),
    DANISH("da"),
    HEBREW("he"),
    YIDDISH("yi"),
    INDONESIAN("id"),
    IRISH("ga"),
    ITALIAN("it"),
    ICELANDIC("is"),
    SPANISH("es"),
    KAZAKH("kk"),
    KANNADA("kn"),
    CATALAN("ca"),
    KYRGYZ("ky"),
    CHINESE("zh"),
    KOREAN("ko"),
    XHOSA("xh"),
    KHMER("km"),
    LAOTIAN("lo"),
    LATIN("la"),
    LATVIAN("lv"),
    LITHUANIAN("lt"),
    LUXEMBOURGISH("lb"),
    MALAGASY("mg"),
    MALAY("ms"),
    MALAYALAM("ml"),
    MALTESE("mt"),
    MACEDONIAN("mk"),
    MAORI("mi"),
    MARATHI("mr"),
    MARI("mhr"),
    MONGOLIAN("mn"),
    GERMAN("de"),
    NEPALI("ne"),
    NORWEGIAN("no"),
    PUNJABI("pa"),
    PAPIAMENTO("pap"),
    PERSIAN("fa"),
    POLISH("pl"),
    PORTUGUESE("pt"),
    ROMANIAN("ro"),
    RUSSIAN("ru"),
    CEBUANO("ceb"),
    SERBIAN("sr"),
    SINHALA("si"),
    SLOVAKIAN("sk"),
    SLOVENIAN("sl"),
    SWAHILI("sw"),
    SUNDANESE("su"),
    TAJIK("tg"),
    THAI("th"),
    TAGALOG("tl"),
    TAMIL("ta"),
    TATAR("tt"),
    TELUGU("te"),
    TURKISH("tr"),
    UDMURT("udm"),
    UZBEK("uz"),
    UKRAINIAN("uk"),
    URDU("ur"),
    FINNISH("fi"),
    FRENCH("fr"),
    HINDI("hi"),
    CROATIAN("hr"),
    CZECH("cs"),
    SWEDISH("sv"),
    SCOTTISH("gd"),
    ESTONIAN("et"),
    ESPERANTO("eo"),
    JAVANESE("jv"),
    JAPANESE("ja");

    private String languageCode;

    YandexLanguage(String languageCode) {
        this.languageCode = languageCode;
    }

    public String getLanguageCode() {
        return languageCode;
    }

    public static YandexLanguage fromLanguageCode(String searchLanguage) {
        for (YandexLanguage language : values()) {
            if (language.languageCode.equals(searchLanguage)) {
                return language;
            }
        }
        return null;
    }
}
