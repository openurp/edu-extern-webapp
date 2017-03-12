/*
 * OpenURP, Agile Development Scaffold and Toolkit
 *
 * Copyright (c) 2005-2015, OpenURP Software.
 *
 * OpenURP is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * OpenURP is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with OpenURP.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.openurp.edu.extern.service.checker;

import java.util.List;

import org.openurp.edu.base.model.Student;
import org.openurp.edu.extern.model.ExamFeeConfig;
import org.openurp.fee.code.model.PayState;
import org.openurp.fee.model.Bill;
import org.openurp.fee.service.PaymentChecker;
import org.openurp.fee.service.impl.PaymentContext;

public class ExamPaymentChecker implements PaymentChecker {

  public String check(PaymentContext context) {
    Student student = context.get("student", Student.class);
    if (null == student) { return "没有权限"; }
    @SuppressWarnings("unchecked")
    List<ExamFeeConfig> configs = (List<ExamFeeConfig>) context.get("feeConfigs");
    if (configs.isEmpty()) { return "在线支付已关闭"; }
    Bill bill = context.getBill();
    if (null == bill) { return "没有找到订单"; }
    if (!bill.inPaymentTime()) { return "订单支付时间未开放或已结束"; }
    if (!PayState.UNPAID.equals(bill.getState().getId())) { return "该订单已支付或退订"; }
    return null;
  }
}
