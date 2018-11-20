[#ftl]
[#list certTypes?sort_by(["code"]) as certType]
  <option value="${certType.id}">${certType.name}</option>
[/#list]
[#if certTypes?size == 0]
  <option value="" style="color: red">（找不到对应的科目/子类）</option>
[/#if]
