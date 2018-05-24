package com.example.funny.theadsnservices;

import android.support.annotation.NonNull;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Translate {

    @NonNull
    public String englToRuss(String _start)
    {
        String _end = _start.toUpperCase();
        Pattern _shchP= Pattern.compile("SHCH"); Matcher _shchM = _shchP.matcher(_end);_end = _shchM.replaceAll("Щ");
        Pattern _yoP= Pattern.compile("YO"); Matcher _yoM = _yoP.matcher(_end);_end = _yoM.replaceAll("Ё");
        Pattern _zhP= Pattern.compile("ZH"); Matcher _zhM = _zhP.matcher(_end);_end = _zhM.replaceAll("Ж");
        Pattern _eeP= Pattern.compile("EE"); Matcher _eeM = _eeP.matcher(_end);_end = _eeM.replaceAll("И");
        Pattern _yuP= Pattern.compile("YU"); Matcher _yuM = _yuP.matcher(_end);_end = _yuM.replaceAll("Ю");
        Pattern _yaP= Pattern.compile("YA"); Matcher _yaM = _yaP.matcher(_end);_end = _yaM.replaceAll("Я");
        //Pattern _iaP= Pattern.compile("IA"); Matcher _iaM = _iaP.matcher(_end);_end = _iaM.replaceAll("Я");
        Pattern _chP= Pattern.compile("CH"); Matcher _chM = _chP.matcher(_end);_end = _chM.replaceAll("Ч");
        Pattern _shP= Pattern.compile("SH"); Matcher _shM = _shP.matcher(_end);_end = _shM.replaceAll("Ш");
        Pattern _tsP= Pattern.compile("TS"); Matcher _tsM = _tsP.matcher(_end);_end = _tsM.replaceAll("Ц");
         Pattern _aeP= Pattern.compile("E"); Matcher _aeM = _aeP.matcher(_end);_end = _aeM.replaceAll("Э");
        Pattern _aP= Pattern.compile("A"); Matcher _aM = _aP.matcher(_end);_end = _aM.replaceAll("А");
        Pattern _bP= Pattern.compile("B"); Matcher _bM = _bP.matcher(_end);_end = _bM.replaceAll("Б");
        Pattern _vP= Pattern.compile("V"); Matcher _vM = _vP.matcher(_end);_end = _vM.replaceAll("В");
        Pattern _wP= Pattern.compile("W"); Matcher _wM = _wP.matcher(_end);_end = _wM.replaceAll("В");
        Pattern _gP= Pattern.compile("G"); Matcher _gM = _gP.matcher(_end);_end = _gM.replaceAll("Г");
        Pattern _dP= Pattern.compile("D"); Matcher _dM = _dP.matcher(_end);_end = _dM.replaceAll("Д");
         Pattern _eP= Pattern.compile("E"); Matcher _eM = _eP.matcher(_end);_end = _eM.replaceAll("Е");
        Pattern _jP= Pattern.compile("J"); Matcher _jM = _jP.matcher(_end);_end = _jM.replaceAll("Й");
        Pattern _zP= Pattern.compile("Z"); Matcher _zM = _zP.matcher(_end);_end = _zM.replaceAll("З");
        Pattern _yP= Pattern.compile("Y"); Matcher _yM = _yP.matcher(_end);_end = _yM.replaceAll("Й");
        Pattern _cP= Pattern.compile("С"); Matcher _cM = _cP.matcher(_end);_end = _cM.replaceAll("К");
        Pattern _lP= Pattern.compile("L"); Matcher _lM = _lP.matcher(_end);_end = _lM.replaceAll("Л");
        Pattern _mP= Pattern.compile("M"); Matcher _mM = _mP.matcher(_end);_end = _mM.replaceAll("М");
        Pattern _nP= Pattern.compile("N"); Matcher _nM = _nP.matcher(_end);_end = _nM.replaceAll("Н");
        Pattern _oP= Pattern.compile("O"); Matcher _oM = _oP.matcher(_end);_end = _oM.replaceAll("O");
        Pattern _pP= Pattern.compile("P"); Matcher _pM = _pP.matcher(_end);_end = _pM.replaceAll("П");
        Pattern _rP= Pattern.compile("R"); Matcher _rM = _rP.matcher(_end);_end = _rM.replaceAll("Р");
        Pattern _sP= Pattern.compile("S"); Matcher _sM = _sP.matcher(_end);_end = _sM.replaceAll("С");
        Pattern _tP= Pattern.compile("T"); Matcher _tM = _tP.matcher(_end);_end = _tM.replaceAll("Т");
        Pattern _uP= Pattern.compile("U"); Matcher _uM = _uP.matcher(_end);_end = _uM.replaceAll("У");
        Pattern _fP= Pattern.compile("F"); Matcher _fM = _fP.matcher(_end);_end = _fM.replaceAll("Ф");
        Pattern _hP= Pattern.compile("H"); Matcher _hM = _hP.matcher(_end);_end = _hM.replaceAll("Х");
        Pattern _iP= Pattern.compile("I"); Matcher _iM = _iP.matcher(_end);_end = _iM.replaceAll("И");
        Pattern _mzP= Pattern.compile("'"); Matcher _mzM = _mzP.matcher(_end);_end = _mzM.replaceAll("Ь");


        return _end.toString();
    }
}

