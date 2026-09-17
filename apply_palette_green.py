import os
import re

ui_dir = 'app/src/main/java/com/example/kalax/ui'

replacements = [
    (r'Color\(0xFF020617\)', 'Color(0xFFF1F5E1)'),
    (r'Color\(0xFF0F172A\)', 'Color(0xFFD5E0B5)'),
    (r'Color\(0xFF1E293B\)', 'Color(0xFFC4D1A4)'),
    (r'Color\(0xFF06B6D4\)', 'Color(0xFF98B891)'),
    (r'Color\(0xFF3B82F6\)', 'Color(0xFF98B891)'),
    (r'Color\(0xFF8B5CF6\)', 'Color(0xFF98B891)'),
    (r'Color\(0xFF22D3EE\)', 'Color(0xFF98B891)'),
    (r'Color\(0xFF00F0FF\)', 'Color(0xFF98B891)'),
    (r'(?<!\w)Color\.White(?!\w)', 'Color(0xFF4A5D44)'),
    (r'(?<!\w)Color\.Gray(?!\w)', 'Color(0xFF697A63)'),
    (r'(?<!\w)Color\.LightGray(?!\w)', 'Color(0xFF697A63)'),
]

for root, dirs, files in os.walk(ui_dir):
    for file in files:
        if file.endswith('.kt'):
            filepath = os.path.join(root, file)
            with open(filepath, 'r', encoding='utf-8') as f:
                content = f.read()
            
            new_content = content
            for old, new in replacements:
                new_content = re.sub(old, new, new_content)
                
            if new_content != content:
                with open(filepath, 'w', encoding='utf-8') as f:
                    f.write(new_content)
                print(f"Updated {file}")

print("Done applying palette.")
