package com.akira.general.commands.interfaces;

import com.akira.general.datas.LabWork;

/**
 * Интерфейс для команд, требующих объект LabWork.
 */
public interface ObjectModable extends Command {
    /**
     * Устанавливает объект лабораторной работы для выполнения команды.
     *
     * @param labWork объект лабораторной работы
     */
    public void setObject(LabWork labWork);
}
