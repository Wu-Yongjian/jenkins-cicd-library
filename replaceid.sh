#!/bin/bash

# 定义要搜索和替换的字符串
search_string="9a922e65-7688-4308-8932-30a2672cb697"
replace_string="9a922e65-7688-4308-8932-30a2672cb697"

# 使用find命令递归查找所有文件，并对每个文件执行替换操作
find . -type f -exec sed -i "s/$search_string/$replace_string/g" {} +

echo "替换完成。"

