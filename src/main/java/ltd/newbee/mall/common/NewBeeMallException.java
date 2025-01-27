/**
 * 严肃声明：
 * 开源版本请务必保留此注释头信息，若删除我方将保留所有法律责任追究！
 * 本软件已申请软件著作权，受国家版权局知识产权以及国家计算机软件著作权保护！
 * 可正常分享和学习源码，不得用于违法犯罪活动，违者必究！
 * Copyright (c) 2019-2021 十三 all rights reserved.
 * 版权所有，侵权必究！
 */
package ltd.newbee.mall.common;

/**
 * 该类为异常类
 *
 * @author 13
 * 自定义异常 由于spring的Exception处理只会管Service层还是Controller层的RuntimeException<br>
 * 所以需要自定义异常来处理其他全局的RuntimeException<br>
 * 我的建议是应该和Handler一起放到一个单独的Exception包下 但他喜欢这样就不动了
 */
public class NewBeeMallException extends RuntimeException {

    public NewBeeMallException() {
        // 空参构造函数
    }

    public NewBeeMallException(String message) {
        super(message);
    }

    /**
     * 丢出一个异常
     *
     * @param message
     */
    public static void fail(String message) {
        throw new NewBeeMallException(message);
    }

}
