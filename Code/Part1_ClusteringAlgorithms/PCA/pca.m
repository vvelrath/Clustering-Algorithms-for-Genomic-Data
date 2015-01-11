%read input file
X = load('C:/Users/Neeti/Desktop/3rd sem/Data Mining/project 2/iyer.txt');
X = X(:,3:end);

%princomp takes a data matrix X 
[pc, score, latent, tsquare] = princomp(X);

%The scores are the data formed by transforming the original data into the space of the principal components
%The first two columns are used to represent data in 2-d space
new_score = score(:,1:2);

%read clustering output file
clusters = load('C:/Users/Neeti/Desktop/3rd sem/Data Mining/project 2/iyer_mapreduce.txt');

%plot(new_score(:,1), new_score(:,2),'.');

%unique_elements = size(unique(clusters));
[m,n] = size(clusters);
%for i = 1:size(clusters)
%    if(clusters(i,1) == -1)
%        clusters(i,1) = unique_elements(1,1);
%        unique_elements(1,1) = unique_elements(1,1) + 1;
%    end
%end

num_of_clusters = size(unique(clusters));
list_of_colors = hsv(num_of_clusters(1,1));

%assign colors to each cluster
color_assignment = zeros(m,3);
for i = 1:m
    if(clusters(i) ~= -1)
        color_assignment(i,:) = list_of_colors(clusters(i),:);
    end
end

%plot each cluster with different color
hold on;
for i = 1:m
    plot(new_score(i,1), new_score(i,2),'Color',color_assignment(i,:),'Marker','.');
end