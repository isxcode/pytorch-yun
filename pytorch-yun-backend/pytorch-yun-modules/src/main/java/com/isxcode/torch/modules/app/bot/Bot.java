package com.isxcode.torch.modules.app.bot;

public abstract class Bot {

    /**
     * 普通多轮对话.
     */
    public abstract void normalChat();

    /**
     * 6️流对话.
     */
    public abstract void streamChat();

    /**
     * 智能体名称.
     */
    public abstract String name();
}
