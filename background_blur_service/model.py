import torch
from PIL import Image, ImageFilter
from torchvision import transforms


def load_model():
    model = torch.hub.load('pytorch/vision:v0.10.0', 'deeplabv3_mobilenet_v3_large', pretrained=True)
    model.eval()
    return model

def blur_background(model, image):

    # preprocessing
    input_image = Image.open(image).convert("RGB")

    # torch processings
    preprocess = transforms.Compose([
        transforms.ToTensor(),
        transforms.Normalize(mean=[0.485, 0.456, 0.406], std=[0.229, 0.224, 0.225]),
    ])

    input_tensor = preprocess(input_image)
    input_batch = input_tensor.unsqueeze(0) # create a mini-batch as expected by the model

    # move the input and model to GPU for speed if available
    if torch.cuda.is_available():
        input_batch = input_batch.to('cuda')
        model.to('cuda')

    # predicting mask for the image
    with torch.no_grad():
        output = model(input_batch)['out'][0]

    output_predictions = output.argmax(0)

    # create a color pallette, selecting a color for each class
    palette = torch.tensor([2 ** 25 - 1, 2 ** 15 - 1, 2 ** 21 - 1])
    colors = torch.as_tensor([i for i in range(21)])[:, None] * palette
    colors = (colors % 255).numpy().astype("uint8")

    # plot the semantic segmentation predictions of 21 classes in each color
    mask = Image.fromarray(output_predictions.byte().cpu().numpy()).resize(input_image.size)
    mask.putpalette(colors)

    # creating black&white mask for back- and foreground 
    bw_mask = mask.convert(mode='L').point(lambda x: 255 if x > 0 else 0, "L")

    # smoothing mask borders
    smooth_bw_mask = bw_mask.filter(ImageFilter.GaussianBlur(radius=5))

    # blurring the whole image
    blurred_image = input_image.filter(ImageFilter.GaussianBlur(radius=15))

    # blending
    blended_image = Image.composite(input_image, blurred_image, smooth_bw_mask)

    return blended_image
