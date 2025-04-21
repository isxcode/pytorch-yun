from transformers import AutoModelForCausalLM, AutoTokenizer
from transformers import AutoConfig

config = AutoConfig.from_pretrained("/Users/ispong/Downloads/qwen-0.5b/config.json")

model_path = "/Users/ispong/Downloads/qwen-0.5b"

model = AutoModelForCausalLM.from_pretrained(
    model_path,
    config=config,
    torch_dtype="auto",
    device_map="auto"
)

tokenizer = AutoTokenizer.from_pretrained(model_path)

prompt = "你是谁？"
messages = [
    {"role": "system", "content": "You are Qwen, created by Alibaba Cloud. You are a helpful assistant."},
    {"role": "user", "content": prompt}
]

text = tokenizer.apply_chat_template(
    messages,
    tokenize=False,
    add_generation_prompt=True
)
model_inputs = tokenizer([text], return_tensors="pt").to(model.device)

generated_ids = model.generate(
    **model_inputs,
    max_new_tokens=512
)

generated_ids = [
    output_ids[len(input_ids):] for input_ids, output_ids in zip(model_inputs.input_ids, generated_ids)
]

response = tokenizer.batch_decode(generated_ids, skip_special_tokens=True)[0]

print(response)