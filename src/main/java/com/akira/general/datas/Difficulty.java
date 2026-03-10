package com.akira.general.datas;

import java.io.Serializable;

/**
 * Перечисление уровней сложности лабораторной работы.
 * <p>
 * Определяет возможные уровни сложности выполнения задания.
 * </p>
 */
public enum Difficulty implements Serializable{
    
    /** Лёгкий уровень сложности */
    EASY,
    /** Сложный уровень сложности */
    HARD,
    /** Очень сложный уровень сложности */
    VERY_HARD,
    /** Безумно сложный уровень сложности */
    INSANE;
}