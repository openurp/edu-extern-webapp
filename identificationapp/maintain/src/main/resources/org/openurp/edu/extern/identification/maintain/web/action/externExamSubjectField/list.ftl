[#ftl]
[@b.head/]
  [#include "/component/certificate/const.ftl"/]
  ${MUST_BE_INNER_FIELD_COMMENT}
  [@b.grid items=fields var="field"]
    [@b.gridbar]
      bar.addItem("${b.text("action.new")}", action.add());
      bar.addItem("${b.text("action.modify")}", action.edit());
      bar.addItem("${b.text("action.delete")}", action.remove("确认要删除吗？"));
    [/@]
    [@b.row]
      [@b.boxcol/]
      [@b.col title="对外字段" property="outerField"/]
      [@b.col title="对内字段" property="innerField"]<span[#if fixedRequestFieldMap?keys?seq_contains(field.innerField)] style="color:red"[#elseif fixedResponseFieldMap?keys?seq_contains(field.innerField)] style="color:blue"[/#if]>${field.innerField}</span>[/@]
      [@b.col title="含义" property="name" width="25%"/]
    [/@]
  [/@]
[@b.foot/]
