[#ftl]
<option value="">${Parameters["empty"]!"全部"}</option>
[#list examTimes?sort_by(["code"]) as examTime]
  <option value="${examTime.id}">${examTime.name}</option>
[/#list]
