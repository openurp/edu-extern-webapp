/*
 * OpenURP, Agile University Resource Planning Solution.
 *
 * Copyright © 2014, The OpenURP Software.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful.
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.beangle.commons.transfer;

import java.util.ArrayList;
import java.util.List;

import org.beangle.commons.collection.CollectUtils;

/**
 * 转换结果
 *
 * @author chaostone
 */
public class TransferResult {

  List<TransferMessage> msgs = new ArrayList<TransferMessage>();

  List<TransferMessage> errs = new ArrayList<TransferMessage>();

  List<Object> confirmEntities = CollectUtils.newArrayList();

  Transfer transfer;

  public void addFailure(String message, Object value) {
    errs.add(new TransferMessage(transfer.getTranferIndex(), message, value));
  }

  public void addMessage(String message, Object value) {
    msgs.add(new TransferMessage(transfer.getTranferIndex(), message, value));
  }

  public boolean hasErrors() {
    return !errs.isEmpty();
  }

  public int errors() {
    return errs.size();
  }

  public List<TransferMessage> getMsgs() {
    return msgs;
  }

  public List<TransferMessage> getErrs() {
    return errs;
  }

  public Transfer getTransfer() {
    return transfer;
  }

  public void setTransfer(Transfer transfer) {
    this.transfer = transfer;
  }

  public List<Object> getConfirmEntities() {
    return confirmEntities;
  }
}
